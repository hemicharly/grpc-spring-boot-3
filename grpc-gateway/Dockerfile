FROM golang:1.21.5-bullseye

ENV PROTOBUF_VERSION=3.14.0
ENV PROTOC_ZIP=protoc-$PROTOBUF_VERSION-linux-x86_64.zip

RUN apt-get update && apt-get install -y unzip protobuf-compiler \
    && curl -OL https://github.com/protocolbuffers/protobuf/releases/download/v$PROTOBUF_VERSION/$PROTOC_ZIP \
    && unzip -o $PROTOC_ZIP -d /usr/local bin/protoc \
    && unzip -o $PROTOC_ZIP -d /usr/local 'include/*' \
    && rm -f $PROTOC_ZIP \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app


RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.3.0 &&\
    go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.32.0 &&\
    go install github.com/grpc-ecosystem/grpc-gateway/v2/protoc-gen-grpc-gateway@v2.18.1 &&\
    go install github.com/grpc-ecosystem/grpc-gateway/v2/protoc-gen-openapiv2@v2.18.1

ENV PATH="${PATH}:/go/bin"