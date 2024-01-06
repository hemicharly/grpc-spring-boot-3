#!/bin/bash

PATH_PROTOS=./proto
PATH_GENERATED=./generated
mkdir -p ${PATH_GENERATED}

echo "Removing old services..."
# shellcheck disable=SC2115
rm -rf ${PATH_GENERATED}/*


echo "Generating services..."
protoc -I ${PATH_PROTOS} \
  --go_out ${PATH_PROTOS} --go_opt paths=source_relative \
  --go-grpc_out ${PATH_PROTOS} --go-grpc_opt paths=source_relative \
  --grpc-gateway_out ${PATH_PROTOS} --grpc-gateway_opt paths=source_relative \
  --openapiv2_out ${PATH_PROTOS} --openapiv2_opt use_go_templates=true \
  ${PATH_PROTOS}/*/*.proto

echo "Copy the files to the generated folder..."
cp -r ${PATH_PROTOS}/* ${PATH_GENERATED}/

find "${PATH_PROTOS}" -type f -name "*.go" -delete && find "${PATH_PROTOS}" -type f -name "*.json" -delete

echo "Services generated."