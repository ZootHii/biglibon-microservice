FROM openjdk:21
WORKDIR book-service
ADD target/book-service-0.0.1-SNAPSHOT.jar book-service-0.0.1-SNAPSHOT.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "book-service-0.0.1-SNAPSHOT.jar"]