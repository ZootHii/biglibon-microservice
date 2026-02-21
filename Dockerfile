FROM docker:27-cli

WORKDIR /opt/biglibon-microservice

COPY .env \
     docker-compose.yaml \
     docker-compose-kafka.yaml \
     docker-compose-database.yaml \
     docker-compose-elasticsearch.yaml \
     docker-compose.image-overrides.yaml \
     launcher.sh \
     ./

RUN chmod +x launcher.sh

EXPOSE 8888 8761 9090 8081 5050 5540 5601

ENTRYPOINT ["sh", "launcher.sh"]
