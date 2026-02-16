# Biglibon Microservice
## 1) What is this project?

Biglibon is a **microservice-based library management backend which uses multiple technologies combined**. It manages:

- books,
- libraries,
- and searchable catalog records.

The project is split into independent services (plus a shared Java library):

- **eureka-server** → service discovery,
- **api-gateway** → single entry point and request routing,
- **book-service** → book CRUD and book events,
- **library-service** → library CRUD and assigning books to libraries,
- **catalog-service** → denormalized read model + text search,
- **shared-library** → common DTOs, Kafka infrastructure, exception handling, and cross-cutting utilities.

At the root, a parent Maven project aggregates all modules.

---

## Core architecture

Think of the system in 3 layers:

1. **Access layer**: API Gateway receives client requests and forwards them.
2. **Business layer**: Book/Library/Catalog services handle domain logic.
3. **Data + Event layer**: MongoDB, PostgreSQL, Elasticsearch store data; Kafka moves events between services.

A request flow example:

1. Client calls API Gateway (`/v1/books`, `/v1/libraries`, `/v1/catalogs`).
2. Gateway routes request to the right service via service discovery.
3. Service stores data in its own DB.
4. Some actions publish Kafka events.
5. Catalog service consumes events and builds/updates searchable catalog records.
6. Catalog records are also synced to Elasticsearch for text search.

---

## Services and responsibilities

### API Gateway

- Public entry point on port `8888`.
- Routes paths:
  - `/v1/books/**` → book-service
  - `/v1/libraries/**` → library-service
  - `/v1/catalogs/**` → catalog-service
- Also forwards actuator paths using `StripPrefix`.

### Eureka Server

- Service registry on port `8761`.
- Other services register themselves and discover each other.

### Book Service

- Manages books in MongoDB.
- Endpoints for create/list/get by id/isbn.
- Publishes `create-book` events to Kafka topic `book-events`.

### Library Service

- Manages libraries in PostgreSQL.
- Can add books to libraries by ISBN.
- Uses **Feign Client** to call book-service when resolving books.
- Publishes `add-book-to-library` events to Kafka topic `library-events`.

### Catalog Service

- Keeps a combined catalog model (book + libraries).
- Consumes events from `book-events` and `library-events`.
- Stores catalog data in MongoDB.
- Indexes catalog documents in Elasticsearch for search.
- Provides text search endpoint (`/v1/catalogs/search/{text}`).

### Shared Library

- Contains shared **DTO classes and response models**.
- Provides common **Kafka event model/dispatcher/producer** support.
- Includes common **exception handlers** and **performance-tracking AOP** annotations.

---

## Technologies used

- **Java 21**
- **Maven (multi-module project)**
- **Spring Boot 3.5.0**
- **Spring Cloud 2025.0.0**
- **Spring Cloud Netflix Eureka**
- **Spring Cloud Gateway (WebFlux)**
- **Spring Cloud OpenFeign**
- **Spring Kafka**
- **Spring Data MongoDB**
- **Spring Data JPA** (for PostgreSQL-backed service)
- **Spring Data Elasticsearch**
- **Spring Boot Actuator**
- **Apache Kafka 4.0.0** (KRaft multi-broker compose topology)
- **Kafka UI: ghcr.io/kafbat/kafka-ui:v1.1.0**
- **MongoDB 8.0.10** (+ mongo-express `1.0.2-18`)
- **PostgreSQL 17.5** (+ pgAdmin4 `9.4.0`)
- **Elasticsearch 9.0.2** (3-node cluster)
- **Kibana 9.0.2**
- **Lombok 1.18.38**
- **MapStruct 1.6.3**
- **Hibernate Validator** `8.0.2.Final`

---

Each domain service owns its data store:

- **book-service** → MongoDB
- **library-service** → PostgreSQL
- **catalog-service** → MongoDB + Elasticsearch index for search

This separation is a typical microservice pattern: each service controls its own schema and persistence.

---

The system uses Kafka for cross-service updates:

- `book-service` emits `create-book` events (`book-events`).
- `library-service` emits `add-book-to-library` events (`library-events`).
- `catalog-service` listens to both topics and updates catalog read models.

This means catalog data is built asynchronously from domain events.

---

Detail notes

- Service ports are dynamic (`server.port: 0`) for business services; gateway and eureka have fixed public ports.
- Routing and service-to-service communication rely on Eureka registration.
- Catalog updates are event-driven, so there can be a short delay before search reflects a write.
- The `shared-library` module reduces duplication across services.

---

### Environment Setup ON DOCKER

- **To Run everything on Docker**
    ```bash
  docker run docker.io/zoothii/biglibon-microservice:master
    ```
This command will create everything and make project up and running, 
it will pull every necessary image along with published services on Docker Hub 
(postgres, pgadmin4, mongo, mongo-express, elasticsearch, kibana, docker.io/apache/kafka, ghcr.io/kafbat/kafka-ui) 
it will take some time XD 

- **If that command fail try**
    ```bash
  docker run --rm -v /var/run/docker.sock:/var/run/docker.sock docker.io/zoothii/biglibon-microservice:master
    ```

#### OR

- **Build Project (we skip contextLoad test runs unit test)**
    ```bash
  mvn clean package
    ```

- **Run All Services on Docker**
    ```bash
  docker-compose \
  -f docker-compose.yaml \
  -f docker-compose-kafka.yaml \
  -f docker-compose-database.yaml \
  -f docker-compose-elasticsearch.yaml \
  build --no-cache \
  && 
  docker-compose \
  -f docker-compose.yaml \
  -f docker-compose-kafka.yaml \
  -f docker-compose-database.yaml \
  -f docker-compose-elasticsearch.yaml \
  up -d
    ```

- **Stop and remove built images and volumes**
    ```bash
    docker-compose \
    -f docker-compose.yaml \
    -f docker-compose-kafka.yaml \
    -f docker-compose-database.yaml \
    -f docker-compose-elasticsearch.yaml \
    down --rmi local -v
    ```

### Environment Setup ON LOCAL

- **Setup DBs, Kafka, and Elasticsearch on Docker**
    ```bash
  docker-compose \
  -f docker-compose-kafka.yaml \
  -f docker-compose-database.yaml \
  -f docker-compose-elasticsearch.yaml up -d
    ```
---

## Run Services in order on Local (it can also work on docker)
1. Run Eureka Server
2. Run API Gateway
3. Run Book Service
4. Run Library Service
5. Run Catalog Service

- **Remove containers and volumes**
    ```bash
  docker-compose \
  -f docker-compose-kafka.yaml \
  -f docker-compose-database.yaml \
  -f docker-compose-elasticsearch.yaml down --rmi local -v
    ```

---

## UI Endpoints
- `http://localhost:8761` → Spring Eureka
- `http://localhost:9090` → Kafka UI
- `http://localhost:8081` → Mongo UI
- `http://localhost:5050` → Postgres UI
- `http://localhost:5601` → Kibana

---

## How the App Works

- First create/add **books**
- Then create **libraries**
- Books can exist without libraries, and libraries can exist without books
- Books must be added to libraries explicitly
- Each book should have a **catalog** → handled via `addOrUpdateBook`
- **Books** are stored in **MongoDB**
- **Libraries** are stored in **PostgreSQL** (example of using another DB)
- **Catalogs** are stored in **MongoDB**, based on book + list of libraries containing that book
- Catalogs are created automatically when `addBookEvent` and `addBookToLibraryEvent` happen via **Kafka**
- Each catalog create/update syncs to **Elasticsearch** as `catalog_index`

---

## API Examples

### Create Book
```http
POST http://localhost:8888/v1/books
Content-Type: application/json

{
  "title": "Gurur ve Önyargı",
  "publicationYear": 1813,
  "author": "Jane Austen",
  "publisher": "İş Bankası",
  "isbn": "531765"
}
```

**Response:**
```json
{
  "id": "6980ac84673e68708ce21844",
  "title": "Gurur ve Önyargı",
  "publicationYear": 1813,
  "author": "Jane Austen",
  "publisher": "İş Bankası",
  "isbn": "531765",
  "createdAt": "2026-02-02T13:54:12.557216Z",
  "updatedAt": "2026-02-02T13:54:12.557216Z"
}
```

---

### Create Library
```http
POST http://localhost:8888/v1/libraries
Content-Type: application/json

{
  "name": "Süleymaniye Yazma Eser Kütüphanesi",
  "city": "İstanbul",
  "phone": "(0212) 520 64 60",
  "bookIsbns": ["111","222","333","XD","531765"]
}
```

**Response:**
```json
{
  "id": 3,
  "name": "Süleymaniye Yazma Eser Kütüphanesi",
  "city": "İstanbul",
  "phone": "(0212) 520 64 60",
  "books": [
    { "id": "6980a466673e68708ce217dc", "title": "Sineklerin Tanrısı", "isbn": "111" },
    { "id": "6980a466673e68708ce217dd", "title": "Hamlet", "isbn": "222" },
    { "id": "6980a466673e68708ce217de", "title": "Cesur Yeni Dünya", "isbn": "333" },
    { "id": "6980ac84673e68708ce21844", "title": "Gurur ve Önyargı", "isbn": "531765" }
  ]
}
```

---

### Add Book to Library by ISBN
```http
POST http://localhost:8888/v1/libraries/books/add/by-isbns
Content-Type: application/json

{
  "libraryId": "3",
  "bookIsbns": ["111","222","333","XD","531765"]
}
```

**Response:** same as above, library updated with books.

---

### Catalog Search
book title, author, publisher and library name, city can be search just with a text in this example "gur"
```http
GET http://localhost:8888/v1/catalogs/search/gur
```

**Response:**
```json
[
  {
    "id": "6980ac85af98f30e0f362c9c",
    "book": {
      "bookId": "6980ac84673e68708ce21844",
      "title": "Gurur ve Önyargı",
      "author": "Jane Austen",
      "publisher": "İş Bankası",
      "isbn": "531765"
    },
    "libraries": [
      {
        "libraryId": 3,
        "name": "Süleymaniye Yazma Eser Kütüphanesi",
        "city": "İstanbul",
        "phone": "(0212) 520 64 60"
      }
    ],
    "createdAt": "2026-02-02T13:54:13.092Z",
    "updatedAt": "2026-02-02T14:03:27.152Z"
  }
]
```

---

## GitHub Actions CI/CD (No deploy yet just create docker image then push to Docker Hub and GHCR)

### Pipeline
1. **CI**: On every `master` push runs `mvn clean verify`.
2. **Docker Build**: On every `master` push, build jars `target/*.jar` and creates images of all services.
3. **Docker Stack Build**: On every `master` push, build and creates all in one `biglibon-microservice` image.
4. **Publish Images**: Every image created published on **Docker Hub** and **GHCR**.
5. **Deploy**: No deploy yet.

---

## Images (`biglibon-microservice`)

### Docker Hub
- `docker.io/zoothii/biglibon-microservice-eureka-server:master`
- `docker.io/zoothii/biglibon-microservice-api-gateway:master`
- `docker.io/zoothii/biglibon-microservice-book-service:master`
- `docker.io/zoothii/biglibon-microservice-library-service:master`
- `docker.io/zoothii/biglibon-microservice-catalog-service:master`

### GHCR
- `ghcr.io/zoothii/biglibon-microservice-eureka-server:master`
- `ghcr.io/zoothii/biglibon-microservice-api-gateway:master`
- `ghcr.io/zoothii/biglibon-microservice-book-service:master`
- `ghcr.io/zoothii/biglibon-microservice-library-service:master`
- `ghcr.io/zoothii/biglibon-microservice-catalog-service:master`

---

## GitHub Secrets

Repo → **Settings → Secrets and variables → Actions**:

- `DOCKERHUB_USERNAME` → Docker Hub Username
- `DOCKERHUB_TOKEN` → Docker Hub Access Token

## TODO or May
idempotency aspect, control with redis*
caching redis
spring retry, retryable*
race condition, thread safety issues*
outbox pattern Debezium + CDC
security authentication JWT/OAuth2
search utility class elasticsearch
opentelemetry grafana observability, monitoring
swagger openapi
Resilience4j retry, rate limiter, timeout
CD deployment missing
es pagination, more details
integration tests, test containers


