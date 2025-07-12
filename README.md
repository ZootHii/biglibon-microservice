run

mvn clean package -DskipTests
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml -f docker-compose-elasticsearch.yaml build --no-cache
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml -f docker-compose-elasticsearch.yaml up -d
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml -f docker-compose-elasticsearch.yaml down --rmi local -v   
-> remove locally build images only and volumes

RUN 1
docker-compose -f docker-compose-elasticsearch.yaml up -d
docker-compose -f docker-compose-elasticsearch.yaml down --rmi local -v

RUN 2
docker-compose -f docker-compose-kafka.yaml up -d
docker-compose -f docker-compose-kafka.yaml down --rmi local -v

RUN 3
docker-compose -f docker-compose-database.yaml up -d
docker-compose -f docker-compose-database.yaml down --rmi local -v

RUN 4
docker-compose -f docker-compose.yaml build --no-cache
docker-compose -f docker-compose.yaml up -d
docker-compose -f docker-compose.yaml down --rmi local -v

docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml -f docker-compose-elasticsearch.yaml down --rmi local -v


ALL SET ENJOY
localhost:9090 -> kafka-ui
localhost:8081 -> mongo-ui
localhost:5050 -> postgres-ui
localhost:5601 -> kibana