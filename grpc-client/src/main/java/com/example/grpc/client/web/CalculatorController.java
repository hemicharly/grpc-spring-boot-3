package com.example.grpc.client.web;

import com.example.grpc.client.dto.BaseErrorResponse;
import com.example.grpc.client.dto.CalculatorRequest;
import com.example.grpc.client.dto.CalculatorResponse;
import com.example.grpc.client.service.CalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@Tag(name = "Calculator", description = "Resources calculator.")
@RequestMapping(value = "/v1/calculator")
@RequiredArgsConstructor
@Validated
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add values.", description = "This endpoint is responsible for calculating two values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful calculated.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "CODE_0001", value = "{\"code\":\"CODE_0001\",\"message\":\"Request body required fields.\"}")})}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "INTERNAL_500", value = "{\"code\":\"INTERNAL_500\",\"message\":\"Internal server error.\"}")})}),
    })
    public Mono<CalculatorResponse> add(@Valid @RequestBody final CalculatorRequest calculatorRequest) {
        return Mono.fromSupplier(() -> calculatorService.add(calculatorRequest)).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/sub")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sub values.", description = "This endpoint is responsible for calculating two values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful calculated.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "CODE_0001", value = "{\"code\":\"CODE_0001\",\"message\":\"Request body required fields.\"}")})}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "INTERNAL_500", value = "{\"code\":\"INTERNAL_500\",\"message\":\"Internal server error.\"}")})}),
    })
    public Mono<CalculatorResponse> sub(@Valid @RequestBody final CalculatorRequest calculatorRequest) {
        return Mono.fromSupplier(() -> calculatorService.sub(calculatorRequest)).subscribeOn(Schedulers.boundedElastic());
    }


    @PostMapping("/multiply")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Multiply values.", description = "This endpoint is responsible for calculating two values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful calculated.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "CODE_0001", value = "{\"code\":\"CODE_0001\",\"message\":\"Request body required fields.\"}")})}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "INTERNAL_500", value = "{\"code\":\"INTERNAL_500\",\"message\":\"Internal server error.\"}")})}),
    })
    public Mono<CalculatorResponse> multiply(@Valid @RequestBody final CalculatorRequest calculatorRequest) {
        return Mono.fromSupplier(() -> calculatorService.multiply(calculatorRequest)).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/divide")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Divide values.", description = "This endpoint is responsible for calculating two values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful calculated.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "CODE_0001", value = "{\"code\":\"CODE_0001\",\"message\":\"Request body required fields.\"}")})}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = {@ExampleObject(name = "INTERNAL_500", value = "{\"code\":\"INTERNAL_500\",\"message\":\"Internal server error.\"}")})}),
    })
    public Mono<CalculatorResponse> divide(@Valid @RequestBody final CalculatorRequest calculatorRequest) {
        return Mono.fromSupplier(() -> calculatorService.divide(calculatorRequest)).subscribeOn(Schedulers.boundedElastic());
    }
}
