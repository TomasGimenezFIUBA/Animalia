# Dockerfile (en la raíz del módulo)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn -pl citizen-service-service -am clean package -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/citizen-service-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
