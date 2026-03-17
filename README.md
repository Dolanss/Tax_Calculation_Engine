# Tax Calculation Engine

REST API in Java 17 + Spring Boot 3 for Brazilian ISS (Imposto Sobre Serviços) municipal tax calculation. Rules are defined per service code (LC 116/2003) and IBGE municipality code, with Redis caching and JWT authentication.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Database | PostgreSQL 16 |
| Cache | Redis 7 (1h TTL) |
| Auth | Spring Security 6 + JWT (JJWT 0.12) |
| Migrations | Flyway |
| Docs | Springdoc OpenAPI / Swagger UI |
| Tests | JUnit 5 + Mockito |
| Build | Maven 3.9 |
| Infra | Docker + Docker Compose |

---

## Project Structure

```
src/
├── main/java/com/taxengine/
│   ├── controller/          # REST endpoints
│   ├── domain/
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic
│   ├── dto/                 # Request / Response objects
│   ├── exception/           # Global error handling
│   ├── security/            # JWT filter + token provider
│   └── config/              # Redis, Security, Swagger config
└── main/resources/
    ├── application.yml
    └── db/migration/        # Flyway SQL scripts
```

---

## Running with Docker Compose

```bash
docker compose up --build
```

This starts three containers: `postgres`, `redis`, and `app` (port 8080).

---

## API Usage

### 1. Authenticate

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"Admin@123"}'
```

Response:
```json
{
  "token": "eyJhbGci...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

### 2. Calculate ISS Tax

```bash
curl -X POST http://localhost:8080/api/v1/calculate \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "serviceCode": "1.01",
    "municipalityCode": "3550308",
    "grossValue": 10000.00
  }'
```

Response:
```json
{
  "serviceCode": "1.01",
  "municipalityCode": "3550308",
  "serviceDescription": "Análise e desenvolvimento de sistemas",
  "aliquot": 2.00,
  "grossValue": 10000.00,
  "issAmount": 200.00,
  "netValue": 9800.00,
  "cachedResult": false,
  "calculatedAt": "2026-04-21T14:30:00"
}
```

> **Formula:** `ISS = grossValue × aliquot / 100` (HALF_UP rounding, 2 decimal places)

---

## Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Public | Obtain JWT token |
| `POST` | `/api/v1/calculate` | Bearer JWT | Calculate ISS tax |
| `GET` | `/swagger-ui.html` | Public | Interactive API docs |
| `GET` | `/api-docs` | Public | OpenAPI JSON spec |

---

## Seeded Service Codes (LC 116/2003)

| Code | Description | Default Aliquot |
|---|---|---|
| `1.01` | Análise e desenvolvimento de sistemas | 2% |
| `1.02` | Programação | 2–3% |
| `1.03` | Processamento e hospedagem de dados | 2–3% |
| `4.01` | Medicina e biomedicina | 5% |
| `17.01` | Assessoria ou consultoria | 4–5% |
| `17.06` | Propaganda e publicidade | 5% |

Municipalities seeded: São Paulo (`3550308`), Rio de Janeiro (`3304557`), Belo Horizonte (`3106200`), Curitiba (`4106902`).

---

## Redis Cache

Tax rules are cached by key `taxRules::{municipalityCode}:{serviceCode}` with a **1-hour TTL**. The aliquot lookup hits PostgreSQL only on cache miss — the ISS arithmetic is applied fresh on each request since the gross value varies per call.

---

## Running Tests

```bash
mvn test
```

Unit tests cover: correct ISS calculation, parameterized aliquot ranges, HALF_UP rounding, `RuleNotFoundException` on missing rules, and repository delegation verification. No real database or Redis required — tests use an in-memory H2 database and simple cache.

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/taxengine` | PostgreSQL JDBC URL |
| `DB_USER` | `taxuser` | Database username |
| `DB_PASS` | `taxpass` | Database password |
| `REDIS_HOST` | `localhost` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |
| `JWT_SECRET` | *(hex string)* | HMAC-SHA signing key |
| `JWT_EXPIRATION_MS` | `86400000` | Token TTL (24h) |

---

## Default Credentials

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | `ROLE_ADMIN` |
| `api_user` | `Admin@123` | `ROLE_USER` |

> Change these before any non-local deployment.
