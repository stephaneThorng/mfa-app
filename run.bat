call mvn clean package -DskipTests
docker compose down -v
docker compose build
docker compose up -d
docker exec -it redis-1 redis-cli -c -p 6379 cluster info