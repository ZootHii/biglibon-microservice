# Biglibon Microservice Use & Environment Setup
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