call mvn clean package -DskipTests
docker compose --env-file .env.local -f docker/docker-compose.redis.cluster.yml -f docker/docker-compose.app.yml down -v
docker compose build
docker compose --env-file .env.local -f docker/docker-compose.redis.cluster.yml -f docker/docker-compose.app.yml up -d
docker exec -it redis-1 redis-cli -c -p 6379 cluster info