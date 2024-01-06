DOCKER_CLI=docker-compose -f docker-compose.cli.yml

generate-pb:
	$(DOCKER_CLI) build &&\
	$(DOCKER_CLI) run --rm bash generate.sh

grcp-gateway-start:
	$(DOCKER_CLI) run --rm go mod tidy &&\
	$(DOCKER_CLI) run --rm --service-ports go run main.go

build-plugin:
	$(DOCKER_CLI) run --rm go build -buildmode=plugin -o grpc-demo.so ./plugin

run:
	./grpc-gateway

container-start:
	mvn clean install &&\
	docker-compose up --build
