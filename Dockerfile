FROM docker:27-cli

WORKDIR /opt/biglibon

COPY .env docker-compose.yaml docker-compose-kafka.yaml docker-compose-database.yaml docker-compose-elasticsearch.yaml docker-compose.image-overrides.yaml ./

EXPOSE 8888 8761 9090 8081 5050 5601

ENTRYPOINT ["sh", "-ec", "set -eu; \
if [ ! -S /var/run/docker.sock ]; then \
  echo 'ERROR: /var/run/docker.sock not found. Run with -v /var/run/docker.sock:/var/run/docker.sock'; \
  exit 1; \
fi; \
COMPOSE_FILES='-f /opt/biglibon/docker-compose.yaml -f /opt/biglibon/docker-compose-kafka.yaml -f /opt/biglibon/docker-compose-database.yaml -f /opt/biglibon/docker-compose-elasticsearch.yaml -f /opt/biglibon/docker-compose.image-overrides.yaml'; \
docker compose $COMPOSE_FILES pull; \
docker compose $COMPOSE_FILES up -d --no-build; \
docker compose $COMPOSE_FILES ps; \
trap 'docker compose $COMPOSE_FILES down -v || true' INT TERM; \
while true; do sleep 30; done"]