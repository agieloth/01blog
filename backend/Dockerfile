# # ── Stage 1 : Build ──────────────────────────────────────────────────────────
# FROM maven:3.9.6-eclipse-temurin-17 AS builder

# WORKDIR /app

# # Cache des dépendances Maven
# COPY pom.xml .
# RUN mvn dependency:go-offline -B

# # Compilation
# COPY src ./src
# RUN mvn package -DskipTests -B

# # ── Stage 2 : Runtime ────────────────────────────────────────────────────────
# FROM eclipse-temurin:17-jre-alpine

# WORKDIR /app

# RUN mkdir -p /app/uploads

# COPY --from=builder /app/target/*.jar app.jar

# EXPOSE 8080

# ENTRYPOINT ["java", "-jar", "app.jar"]



# ============================================================
# Stage 1: Build
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier le pom.xml et télécharger les dépendances (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et builder
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Créer le dossier d'upload
RUN mkdir -p /app/uploads && \
    addgroup -S appgroup && adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app

# Copier le JAR depuis le stage build
COPY --from=build /app/target/studenthub-*.jar /app/app.jar

# Utilisateur non-root (sécurité)
USER appuser

# Port exposé
EXPOSE 8080

# Entrypoint
ENTRYPOINT ["java", "-jar", "/app/app.jar"]