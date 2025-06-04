# 🐾 Animalia Backend System

This repository contains a backend system developed to solve the challenge described in [`docs/Backend Challenge - v2.pdf`](./docs/Backend%20Challenge%20-%20v2.pdf).  
It is built with **Java 21**, **Spring Boot 3.5.0**, **Docker**, and follows a **multi-module** architecture.

---

## 🧱 Modules

The project is organized into the following modules:

### 1. `citizen-command-service`
- A microservice responsible for handling commands and modifying the system state.
- Persists data in **PostgreSQL**.
- Publishes domain events to **Kafka**.

### 2. `citizen-query-service`
- A microservice dedicated to querying and reading data.
- Stores projections in **MongoDB**.
- Caches quarantine-related lookups in **Redis**.
- Subscribes to domain events from **Kafka**.

### 3. `discovery-server`
- A **Spring Cloud Eureka** server used for service discovery.

### 4. `api-gateway`
- Routes requests to the appropriate microservices.
- Works as a reverse proxy and handles routing using **Spring Cloud Gateway**.

### 5. `citizen-common`
- A shared module that contains **Avro event classes** used for Kafka-based communication.
- The Avro schemas are located in:
```

src/main/java/com/tomasgimenez/animalia/avro/

````

---

## 🔄 Communication

The `citizen-command-service` and `citizen-query-service` communicate asynchronously using **Apache Kafka**, leveraging Avro-encoded events defined in `citizen-common`.

---

## 🐳 Docker

All four deployable modules (`citizen-command-service`, `citizen-query-service`, `discovery-server`, `api-gateway`) build their Docker images using the **Jib** plugin.  
This avoids the need for manual `Dockerfile`s, and supports direct image pushes to container registries.

---

## 🧪 Getting Started

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose

### Build the Project

From the root of the project:

```bash
mvn clean install
````

This command:

* Compiles the project
* Generates the Avro event classes
* Packages all services

### Run the System

From the root of the project:

```bash
docker-compose up -d
```

This starts all infrastructure components and microservices.

---

## 📦 Tech Stack Summary

| Layer             | Technology           |
| ----------------- | -------------------- |
| Language          | Java 21              |
| Framework         | Spring Boot 3.5.0    |
| Messaging         | Apache Kafka         |
| Serialization     | Apache Avro          |
| Write DB          | PostgreSQL           |
| Read DB           | MongoDB              |
| Cache             | Redis                |
| Containerization  | Docker + Jib Plugin  |
| Service Discovery | Spring Cloud Eureka  |
| API Gateway       | Spring Cloud Gateway |
| Metrics           | Prometheus           |
| Dashboards        | Grafana              |
