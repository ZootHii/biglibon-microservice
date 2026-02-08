FROM docker:27-dind

WORKDIR /opt/biglibon

COPY .env docker-compose.yaml docker-compose-kafka.yaml docker-compose-database.yaml docker-compose-elasticsearch.yaml docker-compose.image-overrides.yaml ./

EXPOSE 8888 8761 9090 8081 5050 5601

ENTRYPOINT ["sh", "-ec", "dockerd --host=unix:///var/run/docker.sock >/tmp/dockerd.log 2>&1 & \
DOCKERD_PID=$!; \
for i in $(seq 1 120); do \
  if docker info >/dev/null 2>&1; then break; fi; \
  sleep 1; \
  if [ \"$i\" -eq 120 ]; then echo 'Docker daemon could not be started.'; tail -n 100 /tmp/dockerd.log || true; exit 1; fi; \
done; \
COMPOSE_FILES='-f /opt/biglibon/docker-compose.yaml -f /opt/biglibon/docker-compose-kafka.yaml -f /opt/biglibon/docker-compose-database.yaml -f /opt/biglibon/docker-compose-elasticsearch.yaml -f /opt/biglibon/docker-compose.image-overrides.yaml'; \
docker compose $COMPOSE_FILES pull; \
docker compose $COMPOSE_FILES up -d --no-build; \
docker compose $COMPOSE_FILES ps; \
trap 'docker compose $COMPOSE_FILES down -v || true; kill $DOCKERD_PID >/dev/null 2>&1 || true' INT TERM; \
while kill -0 \"$DOCKERD_PID\" >/dev/null 2>&1; do sleep 30; done"]