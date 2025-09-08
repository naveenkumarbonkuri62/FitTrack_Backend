# =========================
# Stage 1: Build the JAR
# =========================
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set working directory inside the build container
WORKDIR /build

# Copy Maven POM file and source code
COPY pom.xml . 
COPY src ./src

# Package the Spring Boot app, skip tests for faster build
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Run the app
# =========================
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the runtime container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /build/target/FitTrack-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
