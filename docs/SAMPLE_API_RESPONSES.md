# Sample API Responses

This document shows the expected request and response formats for the
**User API**. It is automatically picked up by the Swagger UI and can be
used as a contract reference for clients.

## Base URL
```
http://localhost:8080
```

Swagger UI: `http://localhost:8080/swagger-ui.html`
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 1. GET /api/users

### Description
Retrieve the complete list of registered users.

### Request
```http
GET /api/users HTTP/1.1
Host: localhost:8080
Accept: application/json
```

### Response — 200 OK (with users)
```json
[
  {
    "id": 1,
    "name": "Sachin",
    "email": "sachin@example.com"
  },
  {
    "id": 2,
    "name": "John",
    "email": "john@example.com"
  }
]
```

### Response — 200 OK (empty list)
```json
[]
```

### Response — 500 Internal Server Error
```json
{
  "timestamp": "2026-06-22T10:15:30.123Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to retrieve users",
  "path": "/api/users"
}
```

---

## 2. GET /actuator/health

### Request
```http
GET /actuator/health HTTP/1.1
Host: localhost:8080
```

### Response — 200 OK
```json
{
  "status": "UP"
}
```
