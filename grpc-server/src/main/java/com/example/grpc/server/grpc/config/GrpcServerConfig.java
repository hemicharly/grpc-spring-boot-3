package com.example.grpc.server.grpc.config;


import com.example.grpc.server.grpc.CalculatorServiceImpl;
import com.example.grpc.server.grpc.interceptor.GrpcServerInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class GrpcServerConfig {

    @Value("${grpc.server.port}")
    private int port;

    private final CalculatorServiceImpl calculatorService;

    @Bean
    public Server grpcServer() throws IOException {
        return ServerBuilder.forPort(port)
                .addService(calculatorService)
                .intercept(new GrpcServerInterceptor())
                .build()
                .start();
    }
}
