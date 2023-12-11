package com.example.grpc.server.service;

import com.example.common.exceptions.GrpcException;
import com.example.grpc.server.dto.CalculatorRequest;
import com.example.grpc.server.dto.CalculatorResponse;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculatorService {

    public CalculatorResponse add(final CalculatorRequest calculatorRequest) {
        if (calculatorRequest.getNum1() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number1 cannot be less than or equal to 0.");
        }
        if (calculatorRequest.getNum2() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number2 cannot be less than or equal to 0.");
        }

        final var result = calculatorRequest.getNum1() + calculatorRequest.getNum2();

        return CalculatorResponse.builder().result(result).build();
    }

    public CalculatorResponse sub(final CalculatorRequest calculatorRequest) {
        if (calculatorRequest.getNum1() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number1 cannot be less than or equal to 0.");
        }
        if (calculatorRequest.getNum2() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number2 cannot be less than or equal to 0.");
        }

        final var result = calculatorRequest.getNum1() - calculatorRequest.getNum2();

        return CalculatorResponse.builder().result(result).build();
    }


    public CalculatorResponse multiply(final CalculatorRequest calculatorRequest) {
        if (calculatorRequest.getNum1() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number1 cannot be less than or equal to 0.");
        }
        if (calculatorRequest.getNum2() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number2 cannot be less than or equal to 0.");
        }

        final var result = calculatorRequest.getNum1() * calculatorRequest.getNum2();

        return CalculatorResponse.builder().result(result).build();
    }

    public CalculatorResponse divide(final CalculatorRequest calculatorRequest) {
        if (calculatorRequest.getNum1() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number1 cannot be less than or equal to 0.");
        }
        if (calculatorRequest.getNum2() <= 0) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number2 cannot be less than or equal to 0.");
        }

        if (calculatorRequest.getNum1() < calculatorRequest.getNum2()) {
            throw new GrpcException(Status.Code.INVALID_ARGUMENT, "INVALID_NUMBER", "Number1 cannot be less than to Number2.");
        }

        final var result = calculatorRequest.getNum1() / calculatorRequest.getNum2();

        return CalculatorResponse.builder().result(result).build();
    }

}
