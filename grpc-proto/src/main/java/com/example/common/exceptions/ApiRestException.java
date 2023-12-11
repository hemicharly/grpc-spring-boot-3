package com.example.common.exceptions;

import io.grpc.Status;
import lombok.Getter;

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
        this.statusCode = grpcToHttpStatus(status.getCode());
        this.code = errorMessage.length == 2 ? errorMessage[1] : "INTERNAL_ERROR";
        this.message = errorMessage.length == 2 ? errorMessage[0] : status.getDescription();
    }


    public static int grpcToHttpStatus(final Status.Code grpcCode) {
        return switch (grpcCode) {
            case OK -> 200;
            case CANCELLED -> 499;
            case UNKNOWN -> 500;
            case INVALID_ARGUMENT -> 400;
            case DEADLINE_EXCEEDED -> 504;
            case NOT_FOUND -> 404;
            case ALREADY_EXISTS -> 409;
            case PERMISSION_DENIED -> 403;
            case RESOURCE_EXHAUSTED -> 429;
            case FAILED_PRECONDITION -> 412;
            case ABORTED -> 409;
            case OUT_OF_RANGE -> 400;
            case UNIMPLEMENTED -> 501;
            case INTERNAL -> 500;
            case UNAVAILABLE -> 503;
            case DATA_LOSS -> 500;
            case UNAUTHENTICATED -> 401;
            default -> 500;
        };
    }
}
