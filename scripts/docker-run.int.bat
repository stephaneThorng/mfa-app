set COMPOSE_PROJECT_NAME=mfa-app
call mvn clean package -DskipTests

docker compose --env-file .env.int -f scripts/compose.network.yml -f scripts/compose.redis.standalone.yml -f scripts/compose.app.yml down -v
docker compose --env-file .env.int -f scripts/compose.network.yml -f scripts/compose.app.yml build
docker compose --env-file .env.int -f scripts/compose.network.yml -f scripts/compose.redis.standalone.yml -f scripts/compose.app.yml up -d
