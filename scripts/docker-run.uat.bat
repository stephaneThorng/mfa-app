set COMPOSE_PROJECT_NAME=mfa-app
REM call mvn clean package -DskipTests
docker compose --env-file .env.uat -f scripts/compose.network.yml -f scripts/compose.redis.cluster.yml -f scripts/compose.app.yml down -v
docker compose --env-file .env.uat -f scripts/compose.network.yml -f scripts/compose.app.yml build
docker compose --env-file .env.uat -f scripts/compose.network.yml -f scripts/compose.redis.cluster.yml -f scripts/compose.app.yml up -d
timeout /t 5
docker exec -it redis-1 redis-cli -c -p 6379 cluster info