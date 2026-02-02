# Biglibon Microservice Use & Environment Setup
### Environment Setup

- **Setup DBs, Kafka, and Elasticsearch on Docker**
    ```bash
    docker-compose -f docker-compose-kafka.yaml \
               -f docker-compose-database.yaml \
               -f docker-compose-elasticsearch.yaml up -d
    ```
- **Build Project**
    ```bash
        mvn clean package -DskipTests
    ```
---

## Run Services in order on Local (it can also work on docker)
1. Run Eureka Server
2. Run API Gateway
3. Run Book Service
4. Run Library Service
5. Run Catalog Service

---

## UI Endpoints
- `http://localhost:8761` → Spring Eureka
- `http://localhost:9090` → Kafka UI
- `http://localhost:8081` → Mongo UI
- `http://localhost:5050` → Postgres UI
- `http://localhost:5601` → Kibana

---

- **Remove containers and volumes**
    ```bash
    docker-compose -f docker-compose-kafka.yaml \
               -f docker-compose-database.yaml \
               -f docker-compose-elasticsearch.yaml down --rmi local -v
    ```
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



```markdown
# Biglibon Microservice Use & Environment Setup

## Build & Run

```bash
# Build project
mvn clean package -DskipTests

# Build and run all services
docker-compose -f docker-compose.yaml \
               -f docker-compose-kafka.yaml \
               -f docker-compose-database.yaml \
               -f docker-compose-elasticsearch.yaml build --no-cache

docker-compose -f docker-compose.yaml \
               -f docker-compose-kafka.yaml \
               -f docker-compose-database.yaml \
               -f docker-compose-elasticsearch.yaml up -d

# Stop and remove locally built images and volumes
docker-compose -f docker-compose.yaml \
               -f docker-compose-kafka.yaml \
               -f docker-compose-database.yaml \
               -f docker-compose-elasticsearch.yaml down --rmi local -v
```

### Individual Runs

- **RUN 1**
  ```bash
  docker-compose -f docker-compose-elasticsearch.yaml up -d
  docker-compose -f docker-compose-elasticsearch.yaml down --rmi local -v
  ```

- **RUN 2**
  ```bash
  docker-compose -f docker-compose-kafka.yaml up -d
  docker-compose -f docker-compose-kafka.yaml down --rmi local -v
  ```

- **RUN 3**
  ```bash
  docker-compose -f docker-compose-database.yaml up -d
  docker-compose -f docker-compose-database.yaml down --rmi local -v
  ```

- **RUN 4**
  ```bash
  docker-compose -f docker-compose.yaml build --no-cache
  docker-compose -f docker-compose.yaml up -d
  docker-compose -f docker-compose.yaml down --rmi local -v
  ```