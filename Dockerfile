FROM docker:27-dind

WORKDIR /opt/biglibon

COPY .env docker-compose.yaml docker-compose-kafka.yaml docker-compose-database.yaml docker-compose-elasticsearch.yaml docker-compose.image-overrides.yaml ./

EXPOSE 8888 8761 9090 8081 5050 5601

ENTRYPOINT ["sh", "-ec", "set -eu; \
COMPOSE_FILES='-f /opt/biglibon/docker-compose.yaml -f /opt/biglibon/docker-compose-kafka.yaml -f /opt/biglibon/docker-compose-database.yaml -f /opt/biglibon/docker-compose-elasticsearch.yaml -f /opt/biglibon/docker-compose.image-overrides.yaml'; \
if [ -S /var/run/docker.sock ]; then \
  echo 'Using host Docker socket...'; \
else \
  echo 'Host Docker socket not found, trying internal Docker daemon...'; \
  dockerd --host=unix:///var/run/docker.sock --storage-driver=vfs --iptables=false --bridge=none >/tmp/dockerd.log 2>&1 & \
  DOCKERD_PID=$!; \
  for i in $(seq 1 180); do \
    if docker info >/dev/null 2>&1; then break; fi; \
    sleep 1; \
    if [ \"$i\" -eq 180 ]; then \
      echo 'ERROR: Docker daemon could not be started.'; \
      echo 'Option 1 (recommended): docker run --rm -v /var/run/docker.sock:/var/run/docker.sock docker.io/zoothii/biglibon-microservice:master'; \
      echo 'Option 2: docker run --rm --privileged docker.io/zoothii/biglibon-microservice:master'; \
      tail -n 200 /tmp/dockerd.log || true; \
      exit 1; \
    fi; \
  done; \
fi; \
docker compose $COMPOSE_FILES pull --ignore-buildable; \
docker compose $COMPOSE_FILES up -d --no-build; \
docker compose $COMPOSE_FILES ps; \
trap 'docker compose $COMPOSE_FILES down -v || true; if [ -n \"${DOCKERD_PID:-}\" ]; then kill $DOCKERD_PID >/dev/null 2>&1 || true; fi' INT TERM; \
while true; do sleep 30; done"]