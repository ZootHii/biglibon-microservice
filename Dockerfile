FROM docker:27-dind-rootless

WORKDIR /opt/biglibon

COPY .env docker-compose.yaml docker-compose-kafka.yaml docker-compose-database.yaml docker-compose-elasticsearch.yaml docker-compose.image-overrides.yaml ./

EXPOSE 8888 8761 9090 8081 5050 5601

ENTRYPOINT ["sh", "-ec", "set -eu; \
COMPOSE_FILES='-f /opt/biglibon/docker-compose.yaml -f /opt/biglibon/docker-compose-kafka.yaml -f /opt/biglibon/docker-compose-database.yaml -f /opt/biglibon/docker-compose-elasticsearch.yaml -f /opt/biglibon/docker-compose.image-overrides.yaml'; \
(dockerd-entrypoint.sh >/tmp/dockerd.log 2>&1) & \
DOCKERD_PID=$!; \
for i in $(seq 1 180); do \
  if docker info >/dev/null 2>&1; then break; fi; \
  sleep 1; \
  if [ \"$i\" -eq 180 ]; then echo 'Docker daemon could not be started.'; tail -n 200 /tmp/dockerd.log || true; exit 1; fi; \
done; \
docker compose $COMPOSE_FILES pull; \
docker compose $COMPOSE_FILES up -d --no-build; \
docker compose $COMPOSE_FILES ps; \
trap 'docker compose $COMPOSE_FILES down -v || true; kill $DOCKERD_PID >/dev/null 2>&1 || true' INT TERM; \
while kill -0 \"$DOCKERD_PID\" >/dev/null 2>&1; do sleep 30; done"]