package com.example.grpc.client.grpc.calculator.mapper;

import com.example.grpc.client.dto.CalculatorRequest;
import com.example.grpc.client.dto.CalculatorResponse;
import com.example.grpc.models.OperationRequest;
import com.example.grpc.models.OperationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientCalculatorMapper {

    ClientCalculatorMapper MAPPER = Mappers.getMapper(ClientCalculatorMapper.class);

    @Mapping(target = "mergeFrom", ignore = true)
    @Mapping(target = "clearField", ignore = true)
    @Mapping(target = "clearOneof", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    @Mapping(target = "mergeUnknownFields", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    OperationRequest toOperationRequest(final CalculatorRequest calculatorRequest);

    CalculatorResponse toCalculatorResponse(final OperationResponse operationResponse);
}
