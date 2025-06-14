run

mvn clean package -DskipTests
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml build --no-cache
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml up -d
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml down --rmi local -v   
-> remove locally build images only and volumes

ALL SET ENJOY
localhost:9090 -> kafka-ui
localhost:8081 -> mongo-ui
localhost:5050 -> postgres-ui