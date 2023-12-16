package com.example.grpc.client.grpc.calculator.config;

import com.example.grpc.client.grpc.calculator.interceptor.GrpcClientCalculatorInterceptor;
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

    @Bean(name = "managedChannelCalculator")
    public ManagedChannel managedChannelCalculator(final Tracer tracer) {
        return ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .idleTimeout(10, TimeUnit.MINUTES)
                .intercept(new GrpcClientCalculatorInterceptor(tracer))
                .build();
    }

    @Bean
    public CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub(final ManagedChannel managedChannelCalculator) {
        return CalculatorServiceGrpc.newBlockingStub(managedChannelCalculator);
    }

}
