package com.example.grpc.server.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CalculatorRequest {
    private Integer num1;
    private Integer num2;
}
