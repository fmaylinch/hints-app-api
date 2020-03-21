echo "--- Building jar ---"
mvn package

echo "--- Building Docker image ---"
docker build -f src/main/docker/Dockerfile.jvm -t fmaylinch/hints_app_api-jvm .

echo "--- Pushing Docker image ---"
docker push fmaylinch/hints_app_api-jvm

echo "--- Restarting ECS Service ---"
python restart_aws_service.py

