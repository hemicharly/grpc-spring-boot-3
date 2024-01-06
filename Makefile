DOCKER_CLI=docker-compose -f docker-compose.cli.yml
NETWORK_NAME=infra_net

create-network:
	if ! docker network inspect $(NETWORK_NAME) >/dev/null 2>&1; then \
        echo "The network $(NETWORK_NAME) not exist. Creating..."; \
        docker network create $(NETWORK_NAME); \
        echo "The network $(NETWORK_NAME) was created successfully."; \
    else \
        echo "The network $(NETWORK_NAME) already exist."; \
    fi

generate-pb:
	$(DOCKER_CLI) build &&\
	$(DOCKER_CLI) run --rm bash generate.sh

go-mod-tidy:
	$(DOCKER_CLI) run --rm go mod tidy

grpc-gateway-start:
	docker-compose up grpc_gateway

container-start:
	mvn clean install &&\
	docker-compose up --build
