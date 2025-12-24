call mvn clean package -DskipTests
docker compose -f docker-compose-springboot.yml down -v
docker compose -f docker-compose-springboot.yml build
docker compose -f docker-compose-springboot.yml up -d