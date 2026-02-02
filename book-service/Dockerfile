FROM eclipse-temurin:21-jdk
WORKDIR book-service
ADD target/book-service-0.0.1-SNAPSHOT.jar book-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "book-service-0.0.1-SNAPSHOT.jar"]