package com.example.grpcdemo.web;

import com.example.grpcdemo.dto.CalculatorRequest;
import com.example.grpcdemo.dto.CalculatorResponse;
import com.example.grpcdemo.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "v1/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/add")
    public Mono<CalculatorResponse> add(@RequestBody final CalculatorRequest calculatorRequest) {
        return Mono.fromSupplier(() -> calculatorService.add(calculatorRequest));
    }
}
