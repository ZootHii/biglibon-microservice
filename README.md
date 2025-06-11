run

mvn clean package -DskipTests
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml build --no-cache
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml up -d
docker-compose -f docker-compose.yaml -f docker-compose-kafka.yaml -f docker-compose-database.yaml down --rmi local -v   
-> remove locally build images only and volumes

ALL SET ENJOY