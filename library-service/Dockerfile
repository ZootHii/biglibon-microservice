FROM openjdk:17
WORKDIR library-service
ADD target/library-service-0.0.1-SNAPSHOT.jar library-service-0.0.1-SNAPSHOT.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "library-service-0.0.1-SNAPSHOT.jar"]