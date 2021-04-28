# This file can be copied to the VM, for easy restart
# Note that it requires the file /home/fmaylinch/hintcards/privateKey.pem

docker pull cr.yandex/crp81dg788qn7ff84jpi/hints_app_api-jvm
docker stop hint-cards
docker run --name hint-cards -d --rm -p 80:8090 \
        -v /home/fmaylinch/hintcards:/data \
        -e smallrye.jwt.sign.key-location=/data/privateKey.pem \
        -e HINTS_APP_MONGO_URL="$HINTS_APP_MONGO_URL" \
        cr.yandex/crp81dg788qn7ff84jpi/hints_app_api-jvm
echo Follow logs: docker logs -f hint-cards
