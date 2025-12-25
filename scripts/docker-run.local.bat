set COMPOSE_PROJECT_NAME=mfa-app
call mvn clean package -DskipTests
docker compose --env-file .env.local -f scripts/compose.network.yml -f scripts/compose.app.yml down -v
docker compose -f scripts/compose.app.yml build
docker compose --env-file .env.local -f scripts/compose.network.yml -f scripts/compose.app.yml up -d