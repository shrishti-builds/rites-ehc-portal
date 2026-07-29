# Multi-stage Dockerfile for RITES EHC Portal Backend
FROM maven:3.8.6-openjdk-11-slim AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn package -DskipTests -B

FROM openjdk:11-jre-slim
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/ehc-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
