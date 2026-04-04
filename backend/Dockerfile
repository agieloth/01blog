# ── Stage 1 : Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache des dépendances Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilation
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2 : Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]