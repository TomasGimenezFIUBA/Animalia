## 📄 Infraestructura de Microservicios - Proyecto Animalia

Este documento detalla la infraestructura técnica de cada microservicio en el sistema Animalia. Se describen las bases de datos, caches, modos de comunicación, descubrimiento de servicios y configuraciones clave.

---

### 1. Discovery Server (Eureka)

* **Comunicación**:
  * Recibe registros de microservicios
* **Discovery**: Actúa como servidor de descubrimiento
* **Puerto expuesto**: 8761

---

### 2. API Gateway

* **Comunicación**:
  * Sync: Redirige tráfico hacia `citizen-command-service` y `citizen-query-service`
  * Se comunica con `discovery-server` para descubrir servicios
* **Puerto expuesto**: 8081

---

### 3. Citizen Command Service

* **Rol**: Recibir las requests de escritura incluyendo lógica de negocio
* **Base de datos**: PostgreSQL
* **Cache**: En memoria (caffeine)
* **Comunicación**:
  * Sync: Se comunica con Gateway y Eureka
  * Async: Produce eventos al tópico `citizen.event.v1` Kafka de tipo CitizenEvent
* **Puerto expuesto**: 8080

---

### 4. Citizen Query Service

* **Rol**: Recibir las requests de lectura
* **Base de datos**: MongoDB
* **Cache**: Redis
* **Comunicación**:
  * Sync: Se comunica con Gateway y Eureka
  * Async: Consume el tópico `citizen.event.v1` de Kafka 
* **Puerto expuesto**: 8080

---

### 5. Kafka

* **Rol**: Mensajería asíncrona entre microservicios
* **Microservicios relacionados**:
  * Emisor: `citizen-command-service`
  * Receptor: `citizen-query-service`
* **Configuración**:
  * KRaft mode, listeners internos y externos
  * Puertos: 9092 (interno), 29092 (externo)
---

### 6. Redis (Quarantine Cache)

* **Rol**: Cache para el `citizen-query-service`
* **Persistencia**: `appendonly` activado
* **Puerto expuesto**: 6379

---

### 7. Prometheus

* **Rol**: Scrapea métricas de microservicios
* **Microservicios monitoreados**: `citizen-command-service`, `citizen-query-service`
* **Configuración**:

  * Se define en archivo externo `prometheus.yml`
  * Puerto expuesto: 9090

---

### 8. Grafana

* **Rol**: Visualización de métricas obtenidas desde Prometheus
* **Dependencia**: `prometheus`
* **Puerto expuesto**: 3000
* **Credenciales por defecto**:

  * Usuario: admin
  * Contraseña: password


