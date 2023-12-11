package com.example.grpc.client.grpc.mapper;

import com.example.grpc.client.dto.CalculatorRequest;
import com.example.grpc.client.dto.CalculatorResponse;
import com.example.grpc.models.OperationRequest;
import com.example.grpc.models.OperationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CalculatorMapper {

    CalculatorMapper MAPPER = Mappers.getMapper(CalculatorMapper.class);

    OperationRequest toOperationRequest(final CalculatorRequest calculatorRequest);

    CalculatorResponse toCalculatorResponse(final OperationResponse operationResponse);
}
