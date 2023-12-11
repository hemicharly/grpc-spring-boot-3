package com.example.grpcdemo.service;

import com.example.grpcdemo.dto.CalculatorRequest;
import com.example.grpcdemo.dto.CalculatorResponse;
import com.example.grpcdemo.model.AddRequest;
import com.example.grpcdemo.model.CalculatorServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final CalculatorServiceGrpc.CalculatorServiceBlockingStub serviceBlockingStub;

    public CalculatorResponse add(final CalculatorRequest calculatorRequest) {
        final var grpcResponse = serviceBlockingStub.add(AddRequest.newBuilder().setNum1(calculatorRequest.getNum1()).setNum2(calculatorRequest.getNum2()).build());
        return CalculatorResponse.builder().result(grpcResponse.getResult()).build();
    }
}
