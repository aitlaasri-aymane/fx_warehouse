# FXWare - Clustered Data Warehouse Service


![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

---

## 🚀 Getting Started

### Prerequisites
* Docker & Docker Compose
* Java 21 (optional, if running locally without Docker)
* Make (optional, for convenience commands)

### 1. Run via Docker (Recommended)
The easiest way to run the application and the database together.

```bash
# Build the JAR and start containers
make run

# Or manually:
# 1. .\mvnw clean package -DskipTests
# 2. docker-compose up --build
```

The application will start at `http://localhost:8080`.

### 2. Run Tests

```bash
make test

# Or manually:
# .\mvnw test
```

---

## 🔌 API Documentation

### Swagger UI: 👉 **http://localhost:8080/swagger-ui/index.html**

![img.png](https://i.imgur.com/y4kaHSF.png)

### Endpoint: `POST /api/deals/import`

Expects a list of deal objects. Validates per-row constraints (ISO codes, positive amounts) and checks for duplicate IDs before insertion.

**Request Body:**
```json
[
  {
    "dealUniqueId": "FX-100",
    "fromCurrencyIso": "USD",
    "toCurrencyIso": "EUR",
    "dealTimestamp": "2025-11-26T12:00:00",
    "dealAmount": 1500.00
  },
  {
    "dealUniqueId": "FX-INVALID",
    "fromCurrencyIso": "US", 
    "toCurrencyIso": "EUR",
    "dealTimestamp": "2025-11-26T12:00:00",
    "dealAmount": -50.00
  }
]
```

**Response:**
Returns individual status for every item in the batch.

```json
[
  {
    "dealUniqueId": "FX-100",
    "message": "Successfully imported",
    "success": true
  },
  {
    "dealUniqueId": "FX-INVALID",
    "message": "Validation Error: Amount must be greater than 0, From Currency must be a 3-letter ISO code",
    "success": false
  }
]
```

![img_1.png](https://i.imgur.com/ytRHUv5.png)

---

## 🧪 Test Coverage

Tests cover validation logic, duplicate detection, and null handling.

![img_2.png](https://i.imgur.com/MD7zuSU.png)

---

## ⚙️ Deployment & Configuration

* **Local Development:** Connects to `localhost:5432`.
* **Docker Container:** Automatically switches to `postgres-db:5432` via `docker-compose.yml` environment variables.

If you already have a postgres istance running locally on port `5432`, and you want to access the docker container postgres instance, you can access it through the `5433` port.

### Makefile Commands
| Command | Description                                               |
| :--- |:----------------------------------------------------------|
| `make build` | Clean and package JAR (skips tests) |
| `make run` | Rebuild and start containers            |
| `make stop` | Stops and removes containers                              |
| `make test` | Runs the full test suite                                  |
