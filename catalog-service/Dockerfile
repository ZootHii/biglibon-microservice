FROM openjdk:21
WORKDIR catalog-service
ADD catalog-service/target/catalog-service-0.0.1-SNAPSHOT.jar catalog-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "catalog-service-0.0.1-SNAPSHOT.jar"]

#FROM openjdk:21
#WORKDIR catalog-service
#ADD catalog-service/target/catalog-service-0.0.1-SNAPSHOT.jar catalog-service-0.0.1-SNAPSHOT.jar
#
## Elasticsearch CA sertifikasını container içine kopyala (docker-compose ile mount edilmiş olmalı)
## Mount edilen path: .elasticsearch/certs/ca/ca.crt
#COPY .elasticsearch/certs/ca/ca.crt /tmp/ca.crt
#
## CA sertifikasını Java TrustStore'a import et
#RUN keytool -importcert -noprompt \
#    -trustcacerts \
#    -alias elastic-ca \
#    -file /tmp/ca.crt \
#    -keystore $JAVA_HOME/lib/security/cacerts \
#    -storepass changeit
#
## Geçici sertifikayı sil (isteğe bağlı)
#RUN rm /tmp/ca.crt
#
#ENTRYPOINT ["java", "-jar", "catalog-service-0.0.1-SNAPSHOT.jar"]
