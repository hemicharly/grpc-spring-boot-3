package com.example.grpc.server.grpc;

import com.example.grpc.models.CalculatorServiceGrpc;
import com.example.grpc.models.OperationRequest;
import com.example.grpc.models.OperationResponse;
import com.example.grpc.server.service.CalculatorService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.grpc.server.grpc.mapper.CalculatorGrpcMapper.MAPPER;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    private final CalculatorService calculatorService;

    @Override
    public void add(final OperationRequest operationRequest, final StreamObserver<OperationResponse> responseStreamObserver) {
        final var calculatorResponse = calculatorService.add(MAPPER.toCalculatorRequest(operationRequest));
        responseStreamObserver.onNext(MAPPER.toOperationResponse(calculatorResponse));
        responseStreamObserver.onCompleted();
    }

    @Override
    public void sub(final OperationRequest operationRequest, final StreamObserver<OperationResponse> responseStreamObserver) {
        final var calculatorResponse = calculatorService.sub(MAPPER.toCalculatorRequest(operationRequest));
        responseStreamObserver.onNext(MAPPER.toOperationResponse(calculatorResponse));
        responseStreamObserver.onCompleted();
    }

    @Override
    public void multiply(final OperationRequest operationRequest, final StreamObserver<OperationResponse> responseStreamObserver) {
        final var calculatorResponse = calculatorService.multiply(MAPPER.toCalculatorRequest(operationRequest));
        responseStreamObserver.onNext(MAPPER.toOperationResponse(calculatorResponse));
        responseStreamObserver.onCompleted();
    }

    @Override
    public void divide(final OperationRequest operationRequest, final StreamObserver<OperationResponse> responseStreamObserver) {
        final var calculatorResponse = calculatorService.divide(MAPPER.toCalculatorRequest(operationRequest));
        responseStreamObserver.onNext(MAPPER.toOperationResponse(calculatorResponse));
        responseStreamObserver.onCompleted();
    }
}