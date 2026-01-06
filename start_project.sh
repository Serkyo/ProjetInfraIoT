mvn clean package
docker compose up --build -d
docker compose logs -f
# docker-compose --env-file .env up -d --build