# Stage 1: build with Gradle (uses Gradle image with JDK 21)
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy wrapper and build files first for better caching
COPY gradle/ gradle/
COPY gradlew .
COPY settings.gradle.kts build.gradle.kts ./
RUN chmod +x ./gradlew && ./gradlew --no-daemon help || true

# Copy the rest of the project and build
COPY . .
RUN chmod +x ./gradlew && ./gradlew --no-daemon clean bootJar -x test

# Stage 2: runtime with smaller JRE image (Java 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy executable jar from builder
COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
