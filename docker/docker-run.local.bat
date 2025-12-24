call mvn clean package -DskipTests
docker compose --env-file .env.local -f docker/docker-compose.redis.standalone.yml -f docker/docker-compose.app.yml down -v
docker compose build
docker compose --env-file .env.local -f docker/docker-compose.redis.standalone.yml -f docker/docker-compose.app.yml up -d