package com.example.grpc.server.grpc.mapper;

import com.example.grpc.models.OperationRequest;
import com.example.grpc.models.OperationResponse;
import com.example.grpc.server.dto.CalculatorRequest;
import com.example.grpc.server.dto.CalculatorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CalculatorGrpcMapper {
    CalculatorGrpcMapper MAPPER = Mappers.getMapper(CalculatorGrpcMapper.class);

    CalculatorRequest toCalculatorRequest(final OperationRequest operationRequest);

    OperationResponse toOperationResponse(final CalculatorResponse calculatorResponse);
}
