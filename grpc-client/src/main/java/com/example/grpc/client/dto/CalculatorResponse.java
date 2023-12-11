package com.example.grpcdemo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CalculatorResponse {
    private Integer result;
}
