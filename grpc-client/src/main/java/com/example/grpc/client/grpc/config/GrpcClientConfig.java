package com.example.grpc.client.grpc.config;

import com.example.grpc.client.grpc.interceptor.GrpcClientInterceptor;
import com.example.grpc.models.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc-client.client1.host}")
    private String grpcHost;

    @Value("${grpc-client.client1.port}")
    private int grpcPort;

    @Bean
    public GrpcClientInterceptor grpcClientInterceptor(final Tracer tracer){
        return new GrpcClientInterceptor(tracer);
    }

    @Bean
    public ManagedChannel managedChannel(final GrpcClientInterceptor grpcClientInterceptor) {
        return ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .idleTimeout(10, TimeUnit.MINUTES)
                .intercept(grpcClientInterceptor)
                .build();
    }

    @Bean
    public CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub(final ManagedChannel managedChannel) {
        return CalculatorServiceGrpc.newBlockingStub(managedChannel);
    }

}
