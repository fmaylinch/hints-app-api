# HintsApp API Server

API Server for the HintsApp.


## Creation of the project

I followed these guides:
- [Quarkus Getting Started](https://quarkus.io/guides/getting-started)
- [MongoDB guide](https://quarkus.io/guides/mongodb)

## Running the application

Run tests with `./mvnw test`.

Run the application with `./mvnw compile quarkus:dev`.
Then you can attach debugger on port `5005`.
See [development mode](https://quarkus.io/guides/getting-started#development-mode). 

See [Dockerfile.jvm](./src/main/docker/Dockerfile.jvm) on how to build and run with Docker.

## Sample calls

```bash
# Login to get JWT
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"some@email.com", "password":"the-pwd"}' \
  http://127.0.0.1:8090/security/login | jq

# Same command, but sets JWT variable with the token
JWT=$(curl -X POST --silent \
  -H "Content-Type: application/json" \
  -d '{"email":"some@email.com", "password":"the-pwd"}' \
  http://127.0.0.1:8090/security/login | jq -r .password)

# Get all cards using JWT
curl -X POST \
  -H "Authorization: Bearer $JWT" \
  http://127.0.0.1:8090/cards/getAll | jq
```

See about [curl options](https://gist.github.com/subfuzion/08c5d85437d5d4f00e58).
