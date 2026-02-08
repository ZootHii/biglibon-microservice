#!/bin/sh
set -eu

#Elasticsearch certs
#to fix mount error because of docker in docker, creating temp volume just for launcher.
# Normally elastic-search certs created and mounted under biglibon-microservice/.elasticsearch
#Error response from daemon: mounts denied:
#The path /opt/biglibon-microservice/.elasticsearch/certs is not shared from the host and is not known to Docker.
cat > /tmp/docker-compose.launcher.override.yaml <<'YAML'
services:
  catalog-service:
    volumes:
      - launcher-certs:/catalog-service/elasticsearch/certs:ro
  elastic-setup:
    volumes:
      - launcher-certs:/usr/share/elasticsearch/config/certs
  elastic-1:
    volumes:
      - launcher-certs:/usr/share/elasticsearch/config/certs
      - elastic-1-data:/usr/share/elasticsearch/data
  elastic-2:
    volumes:
      - launcher-certs:/usr/share/elasticsearch/config/certs
      - elastic-2-data:/usr/share/elasticsearch/data
  elastic-3:
    volumes:
      - launcher-certs:/usr/share/elasticsearch/config/certs
      - elastic-3-data:/usr/share/elasticsearch/data
  kibana:
    volumes:
      - launcher-certs:/usr/share/kibana/config/certs
      - kibana-data:/usr/share/kibana/data

volumes:
  launcher-certs:
YAML

COMPOSE_FILES="-f /opt/biglibon-microservice/docker-compose.yaml \
-f /opt/biglibon-microservice/docker-compose-kafka.yaml \
-f /opt/biglibon-microservice/docker-compose-database.yaml \
-f /opt/biglibon-microservice/docker-compose-elasticsearch.yaml \
-f /opt/biglibon-microservice/docker-compose.image-overrides.yaml \
-f /tmp/docker-compose.launcher.override.yaml"

if [ ! -S /var/run/docker.sock ]; then
    echo "ERROR: Host Docker socket (/var/run/docker.sock) not found."
    echo "Image needs to run Docker with host socket:"
    echo "Run the container with: docker run --rm -v /var/run/docker.sock:/var/run/docker.sock docker.io/zoothii/biglibon-microservice:master"
    exit 1
fi

echo "Using host Docker socket..."

docker compose $COMPOSE_FILES pull --ignore-buildable
docker compose $COMPOSE_FILES up -d --no-build
docker compose $COMPOSE_FILES ps

trap 'docker compose $COMPOSE_FILES down -v || true' INT TERM

while true; do sleep 30; done
