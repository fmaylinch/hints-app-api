echo "--- Building jar ---"
mvn package

echo "--- Building Docker image ---"
docker build -f src/main/docker/Dockerfile.jvm -t cr.yandex/crp81dg788qn7ff84jpi/hints_app_api-jvm .

echo "--- Selecting profile fm ---"
yc container registry configure-docker --profile fm

echo "--- Pushing Docker image ---"
docker push cr.yandex/crp81dg788qn7ff84jpi/hints_app_api-jvm

echo "--- Pull docker image from VM and run it ---"
echo "$ ssh fmaylinch@130.193.46.10"
echo "$ docker pull cr.yandex/crp81dg788qn7ff84jpi/hints_app_api-jvm"
echo "$ docker ps"
echo "$ docker stop hint-cards"
echo "$ docker run --name hint-cards -d --rm -p 80:8090 -e HINTS_APP_MONGO_URL=\"\$HINTS_APP_MONGO_URL\" cr.yandex/crp81dg788qn7ff84jpi/hints_app_api-jvm"
echo "Access to http://130.193.46.10 or http://hintcards.site"
