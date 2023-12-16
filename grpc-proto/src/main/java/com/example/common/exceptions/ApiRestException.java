package com.example.common.exceptions;

import io.grpc.Status;
import lombok.Getter;

import java.util.List;

import static com.example.common.converters.GrpcConverter.builderMessageDefault;
import static com.example.common.converters.GrpcConverter.grpcCodeToHttpStatusCode;
import static java.util.Objects.isNull;

@Getter
public class ApiRestException extends RuntimeException {
    private final int statusCode;
    private final String code;
    private final String message;

    public ApiRestException(final Exception e) {
        super(e.getMessage());
        final var status = Status.fromThrowable(e);
        if (isNull(status) || isNull(status.getDescription())) {
            this.statusCode = 500;
            this.code = "INTERNAL_ERROR";
            this.message = "Internal server error";
            return;
        }

        final var errorMessage = status.getDescription().split("\n");
        this.statusCode = grpcCodeToHttpStatusCode(status.getCode());
        this.code = errorMessage.length == 2 ? errorMessage[1] : "INTERNAL_ERROR";
        if (List.of(401, 429, 499, 500, 503, 504).contains(this.statusCode)) {
            this.message = builderMessageDefault(this.statusCode);
            return;
        }
        this.message = errorMessage.length == 2 ? errorMessage[0] : status.getDescription();
    }

}
