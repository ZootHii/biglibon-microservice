# Use official Java runtime as base image
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory inside the container
WORKDIR library-service

# Copy Maven dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project and build
COPY . .
RUN mvn clean package -DskipTests

# Use a smaller image for running the app
FROM eclipse-temurin:17-jre-alpine

WORKDIR library-service

# Copy only the built JAR from the previous stage
COPY --from=builder /library-service/target/library-service.jar .

# Expose the required port
EXPOSE 4024

# Define entry point
CMD ["java", "-jar", "library-service.jar"]