FROM docker:27-cli

WORKDIR /opt/biglibon

COPY .env docker-compose.yaml docker-compose-kafka.yaml docker-compose-database.yaml docker-compose-elasticsearch.yaml docker-compose.image-overrides.yaml ./

EXPOSE 8888 8761 9090 8081 5050 5601

ENTRYPOINT ["sh", "-ec", "set -eu; \
COMPOSE_FILES='-f /opt/biglibon/docker-compose.yaml -f /opt/biglibon/docker-compose-kafka.yaml -f /opt/biglibon/docker-compose-database.yaml -f /opt/biglibon/docker-compose-elasticsearch.yaml -f /opt/biglibon/docker-compose.image-overrides.yaml'; \
if [ ! -S /var/run/docker.sock ]; then \
  echo 'ERROR: Host Docker socket (/var/run/docker.sock) bulunamadi.'; \
  echo 'Bu image Docker-in-Docker baslatmaz; host socket ile calisir.'; \
  echo 'Calistirma ornegi:'; \
  echo 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock docker.io/zoothii/biglibon-microservice:master'; \
  exit 1; \
fi; \
echo 'Using host Docker socket...'; \
docker compose $COMPOSE_FILES pull --ignore-buildable; \
docker compose $COMPOSE_FILES up -d --no-build; \
docker compose $COMPOSE_FILES ps; \
trap 'docker compose $COMPOSE_FILES down -v || true' INT TERM; \
while true; do sleep 30; done"]