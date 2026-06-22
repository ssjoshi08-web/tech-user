# User API — Spring Boot 3 Enterprise Reference

A production-ready REST API built with **Java 21**, **Spring Boot 3.x**, and
**Maven**, demonstrating a clean layered architecture, comprehensive
testing, automated security scanning, AI-assisted code review, and a
self-hosted deployment pipeline.

---

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Build & Run](#build--run)
- [API Reference](#api-reference)
- [Testing](#testing)
- [Configuration](#configuration)
- [CI/CD Pipeline](#cicd-pipeline)
- [Deployment](#deployment)
- [Security](#security)
- [Documentation](#documentation)

---

## Features
- ✅ Java 21 + Spring Boot 3.3
- ✅ Layered architecture (controller → service → repository)
- ✅ Constructor injection everywhere — no field injection
- ✅ JPA entity + DTO separation
- ✅ Centralized exception handling with `@RestControllerAdvice`
- ✅ OpenAPI / Swagger UI
- ✅ SLF4J structured logging
- ✅ Lombok for boilerplate reduction
- ✅ Spring Boot Actuator with `/actuator/health`
- ✅ JUnit 5 + Mockito + AssertJ unit tests
- ✅ JaCoCo coverage report with 85% minimum gate
- ✅ GitHub Actions CI/CD (Build → Test → Security → AI Review → Deploy)
- ✅ Trivy filesystem, source, and dependency scanning
- ✅ Automated rollback on health-check failure

---

## Architecture
```
            ┌─────────────────┐
            │  HTTP Client    │
            └────────┬────────┘
                     │
            ┌────────▼────────┐
            │  Controller     │   ← DTOs in/out
            └────────┬────────┘
                     │
            ┌────────▼────────┐
            │  Service        │   ← Business logic
            └────────┬────────┘
                     │
            ┌────────▼────────┐
            │  Repository     │   ← Spring Data JPA
            └────────┬────────┘
                     │
            ┌────────▼────────┐
            │  Database       │
            └─────────────────┘
```

Cross-cutting concerns (error handling, OpenAPI metadata) are implemented
as separate `config` and `exception` components and do not leak into the
domain layers.

---

## Project Structure
```
.
├── .github/
│   └── workflows/
│       ├── blank.yml
│       └── ci-cd.yml                # Full pipeline
├── docs/
│   └── SAMPLE_API_RESPONSES.md
├── scripts/
│   └── deploy.sh                     # Self-hosted runner deploy script
├── src/
│   ├── main/
│   │   ├── java/com/example/userapi/
│   │   │   ├── UserApiApplication.java
│   │   │   ├── controller/UserController.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   └── impl/UserServiceImpl.java
│   │   │   ├── repository/UserRepository.java
│   │   │   ├── entity/User.java
│   │   │   ├── dto/UserDto.java
│   │   │   ├── config/OpenApiConfig.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── UserServiceException.java
│   │   │       └── ErrorResponse.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/example/userapi/
│           ├── controller/UserControllerTest.java
│           └── service/impl/UserServiceImplTest.java
├── claude.md                         # AI review policy
├── review-agent.md                   # AI agent runtime config
├── pom.xml
└── README.md
```

---

## Prerequisites
- **JDK 21** (Temurin or equivalent)
- **Maven 3.9+**
- **Git**

Verify locally:
```bash
java -version    # expect 21.x
mvn -version
```

---

## Build & Run

### Run unit tests
```bash
mvn clean test
```

### Build a runnable jar
```bash
mvn clean package
```

### Run the application
```bash
java -jar target/user-api.jar
```

The API will be available at `http://localhost:8080`.

### Run with Maven
```bash
mvn spring-boot:run
```

---

## API Reference

| Method | Path             | Description           |
|--------|------------------|-----------------------|
| GET    | `/api/users`     | Retrieve all users    |
| GET    | `/actuator/health` | Liveness probe     |
| GET    | `/swagger-ui.html` | Interactive API doc |
| GET    | `/v3/api-docs`   | OpenAPI JSON spec     |

### Sample request
```bash
curl -s http://localhost:8080/api/users | jq
```

### Sample response
```json
[
  { "id": 1, "name": "Sachin", "email": "sachin@example.com" },
  { "id": 2, "name": "John",   "email": "john@example.com"   }
]
```

See [docs/SAMPLE_API_RESPONSES.md](docs/SAMPLE_API_RESPONSES.md) for
the full request/response contract including error envelopes.

---

## Testing

- **Framework:** JUnit 5 + Mockito + AssertJ
- **Web layer:** `MockMvc` (standalone setup with `GlobalExceptionHandler`)
- **Coverage:** JaCoCo (85% line minimum enforced in the build)

Run all tests:
```bash
mvn test
```

Generate the coverage report:
```bash
mvn verify
# Report: target/site/jacoco/index.html
```

### Test Inventory
- `UserServiceImplTest`
  - `getAllUsers_success`
  - `getAllUsers_emptyList`
  - `getAllUsers_repositoryException`
- `UserControllerTest`
  - `verifyGetAllUsers`
  - `verifyStatusCode200`
  - `verifyResponseBody`
  - `verifyResponseBody_empty`
  - `verifyErrorResponse`

---

## Configuration

All runtime configuration lives in
[`src/main/resources/application.yml`](src/main/resources/application.yml):

| Concern           | Value (default)         |
|-------------------|-------------------------|
| Server port       | `8080`                  |
| Logging pattern   | Console + rolling file  |
| Actuator          | `health, info, metrics` |
| Swagger UI        | `/swagger-ui.html`      |
| OpenAPI JSON      | `/v3/api-docs`          |

---

## CI/CD Pipeline

`.github/workflows/ci-cd.yml` defines five sequential stages:

| # | Stage         | Purpose                                              |
|---|---------------|------------------------------------------------------|
| 1 | **Build**     | Checkout → JDK 21 → `mvn clean compile package`     |
| 2 | **Test**      | JUnit 5 + JaCoCo, fail if coverage < 80%             |
| 3 | **Security**  | Trivy (fs, config, vuln) — fail on CRITICAL / HIGH   |
| 4 | **AI Review** | Claude reviews the diff against `review-agent.md`   |
| 5 | **Deploy**    | Self-hosted runner, health-check, auto-rollback      |

Deployment is gated on all four preceding jobs succeeding.

### Required Secrets
| Secret                | Purpose                                  |
|-----------------------|------------------------------------------|
| `ANTHROPIC_API_KEY`   | Enables Stage 4 (AI Review)              |

### Required Runners
- GitHub-hosted `ubuntu-latest` for stages 1–4.
- Self-hosted `ubuntu` runner (label `self-hosted`, `linux`, `ubuntu`)
  for stage 5.

---

## Deployment

`scripts/deploy.sh` implements the same steps the pipeline uses and is
safe to run on any self-hosted host:

```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

The script:
1. Pulls the latest code.
2. Builds the jar.
3. Backs up the previous version.
4. Stops the running app.
5. Deploys the new jar.
6. Starts the new app.
7. Verifies `/actuator/health` (60-second timeout).
8. Rolls back automatically on health-check failure.

---

## Security

- **Trivy** scans the filesystem, configuration, and dependencies on every
  push. Pipeline fails on any `CRITICAL` or `HIGH` vulnerability.
- **AI review** flags hardcoded secrets, sensitive data in logs, and
  stack-trace leakage.
- **Stack traces** are never returned to API clients; only the
  `ErrorResponse` envelope is exposed.
- **Constructor injection** removes the attack surface associated with
  field-injection reflection access.

---

## Documentation

- [Sample API Responses](docs/SAMPLE_API_RESPONSES.md) — request/response
  contract reference.
- [claude.md](claude.md) — AI review policy.
- [review-agent.md](review-agent.md) — AI review agent runtime
  configuration.

---

## License
Apache 2.0 — see `pom.xml` for details.
