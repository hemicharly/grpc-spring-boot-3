package com.example.grpc.client.grpc.calculator.config;

import com.example.common.interceptors.GrpcClientInterceptor;
import com.example.grpc.models.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientCalculatorConfig {

    @Value("${grpc-client.calculator.host}")
    private String grpcHost;

    @Value("${grpc-client.calculator.port}")
    private int grpcPort;

    private static final String GRPC_CLIENT_NAME = "calculator-client";
    private static final String GRPC_SERVER_NAME = "calculator-server";

    @Bean(name = "managedChannelCalculator")
    public ManagedChannel managedChannelCalculator(final Tracer tracer) {
        return ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .idleTimeout(10, TimeUnit.MINUTES)
                .intercept(new GrpcClientInterceptor(GRPC_CLIENT_NAME, GRPC_SERVER_NAME, tracer))
                .build();
    }

    @Bean
    public CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub(final ManagedChannel managedChannelCalculator) {
        return CalculatorServiceGrpc.newBlockingStub(managedChannelCalculator);
    }

}
