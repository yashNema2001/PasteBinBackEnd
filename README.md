

# Pastebin-Lite Backend

A lightweight **Pastebin-like application** that allows users to create text pastes, share them using a unique URL, and apply optional constraints such as expiration time and view limits.

---

## Project Description

This application exposes a **RESTful API** to manage text snippets. It supports:

* **Arbitrary text pastes**
* **Time-based expiry (TTL)**
* **View-count limits**
* **Deterministic time testing** using custom request headers

---

## Persistence Layer

This project uses **Redis** as the persistence layer.

### Why Redis?

Redis was chosen because it:

* Supports **high-performance, atomic operations** (e.g., view-count decrements)
* Is well-suited for **serverless-friendly environments**
* Ensures paste data **persists across independent requests**

---

## Getting Started

### Prerequisites

* Java 17 or higher
* Maven 3.6+
* A running Redis instance (Local or Upstash)

---

### Local Setup

1. **Clone the repository**

2. **Configure environment variables** in
   `src/main/resources/application.properties`

   ```properties
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   TEST_MODE=1
   app.base-url=http://localhost:5173
   ```

3. **Run the application** using Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

---

## API Endpoints

* **GET `/api/healthz`**
  Returns HTTP 200 if the application and Redis are healthy.

* **POST `/api/pastes`**
  Creates a new paste with optional `ttl_seconds` and `max_views`.

* **GET `/api/pastes/{id}`**
  Fetches paste content and metadata.
  Returns **404 Not Found** if:

  * The paste has expired, or
  * The view limit has been reached.

---

## Design Decisions

* **Deterministic Expiry**
  When `TEST_MODE=1` is enabled, the application honors the
  `x-test-now-ms` request header, allowing precise control over time-based logic during automated testing.

* **Failure Consistency**
  All unavailable cases (missing, expired, or exhausted views) consistently return a
  **404 Not Found** response with a JSON body.

* **Safe Rendering**
  The backend returns raw content via the API, ensuring the frontend can render it safely without executing scripts.

---
