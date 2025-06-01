# Use official Java runtime as base image
#FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory inside the container
#WORKDIR api-gateway

# Copy Maven dependencies first (for caching)
#COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy the entire project and build
#COPY . .
#RUN mvn clean package -DskipTests

# Use a smaller image for running the app
FROM maven:3.9.6-eclipse-temurin-17

WORKDIR eureka-server

#COPY . .
# Copy only the built JAR from the previous stage
#COPY --from=builder /api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar .
COPY ./ .
ADD target/eureka-server-0.0.1-SNAPSHOT.jar eureka-server-0.0.1-SNAPSHOT.jar


# Expose the required port
EXPOSE 8761

# Define entry point
ENTRYPOINT ["java", "-jar", "eureka-server-0.0.1-SNAPSHOT.jar"]
CMD ["mvn", "spring-boot:run"]