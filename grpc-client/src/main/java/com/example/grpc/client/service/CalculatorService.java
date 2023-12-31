package com.example.grpc.client.service;

import com.example.common.exceptions.ApiRestException;
import com.example.grpc.client.dto.CalculatorRequest;
import com.example.grpc.client.dto.CalculatorResponse;
import com.example.grpc.models.CalculatorServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.grpc.client.grpc.calculator.mapper.ClientCalculatorMapper.MAPPER;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculatorService {

    private final CalculatorServiceGrpc.CalculatorServiceBlockingStub serviceBlockingStub;

    public CalculatorResponse add(final CalculatorRequest calculatorRequest) {
        try {
            final var addResponse = serviceBlockingStub
                    .withDeadlineAfter(2, TimeUnit.SECONDS)
                    .add(MAPPER.toOperationRequest(calculatorRequest));
            return MAPPER.toCalculatorResponse(addResponse);
        } catch (final Exception e) {
            log.error("Error execute function add: ", e);
            throw new ApiRestException(e);
        }
    }

    public CalculatorResponse sub(final CalculatorRequest calculatorRequest) {
        try {
            final var addResponse = serviceBlockingStub
                    .withDeadlineAfter(2, TimeUnit.SECONDS)
                    .sub(MAPPER.toOperationRequest(calculatorRequest));
            return MAPPER.toCalculatorResponse(addResponse);
        } catch (final Exception e) {
            log.error("Error execute function multiply: ", e);
            throw new ApiRestException(e);
        }
    }

    public CalculatorResponse multiply(final CalculatorRequest calculatorRequest) {
        try {
            final var addResponse = serviceBlockingStub
                    .withDeadlineAfter(2, TimeUnit.SECONDS)
                    .multiply(MAPPER.toOperationRequest(calculatorRequest));
            return MAPPER.toCalculatorResponse(addResponse);
        } catch (final Exception e) {
            log.error("Error execute function multiply: ", e);
            throw new ApiRestException(e);
        }
    }

    public CalculatorResponse divide(final CalculatorRequest calculatorRequest) {
        try {
            final var addResponse = serviceBlockingStub
                    .withDeadlineAfter(2, TimeUnit.SECONDS)
                    .divide(MAPPER.toOperationRequest(calculatorRequest));
            return MAPPER.toCalculatorResponse(addResponse);
        } catch (final Exception e) {
            log.error("Error execute function divide: ", e);
            throw new ApiRestException(e);
        }
    }
}
