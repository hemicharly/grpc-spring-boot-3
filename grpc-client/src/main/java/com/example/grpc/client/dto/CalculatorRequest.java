package com.example.grpc.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CalculatorRequest {

    @Schema(description = "This number 1", example = "10")
    @NotNull(message = "num1 is required")
    private Integer num1;

    @Schema(description = "This number 2", example = "12")
    @NotNull(message = "num2 is required")
    private Integer num2;
}
