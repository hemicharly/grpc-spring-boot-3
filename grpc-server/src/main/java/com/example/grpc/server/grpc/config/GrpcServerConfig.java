package com.example.grpc.server.grpc.config;


import com.example.grpc.server.grpc.CalculatorServiceImpl;
import com.example.grpc.server.grpc.interceptor.GrpcServerInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port}")
    private int port;

    @Bean
    public GrpcServerInterceptor grpcServerInterceptor() {
        return new GrpcServerInterceptor();
    }

    @Bean
    public Server grpcServer(final CalculatorServiceImpl calculatorService, final GrpcServerInterceptor grpcServerInterceptor) throws IOException {
        return ServerBuilder.forPort(port)
                .addService(calculatorService)
                .intercept(grpcServerInterceptor)
                .build()
                .start();
    }
}
