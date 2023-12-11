package com.example.grpc.client.web.handler;


import com.example.common.exceptions.ApiRestException;
import com.example.grpc.client.dto.BaseErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {ApiRestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "CODE_0001", value = "{\"code\":\"CODE_0001\",\"message\":\"Request body required fields.\"}")})}
    )
    ResponseEntity<BaseErrorResponse> handlerBadRequestException(final ApiRestException e) {
        if (e.getStatusCode() == 500) {
            return handlerInternalServerException(e);
        }
        return ResponseEntity.status(e.getStatusCode()).body(new BaseErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "INTERNAL_500", value = "{\"code\":\"INTERNAL_500\",\"message\":\"Internal Server Error.\"}")})}
    )
    ResponseEntity<BaseErrorResponse> handlerInternalServerException(final RuntimeException e) {
        return ResponseEntity.status(500).body(new BaseErrorResponse("INTERNAL_500", "Internal server error."));
    }

}
