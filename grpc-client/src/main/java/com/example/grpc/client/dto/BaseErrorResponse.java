package com.example.grpc.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseErrorResponse {

    @Schema(description = "Operation error code.", example = "CODE_0001")
    private String code;

    @Schema(description = "Operation error message.", example = "Request body required fields.")
    private String message;
}
