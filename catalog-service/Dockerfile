FROM openjdk:21
WORKDIR catalog-service
ADD target/catalog-service-0.0.1-SNAPSHOT.jar catalog-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "catalog-service-0.0.1-SNAPSHOT.jar"]