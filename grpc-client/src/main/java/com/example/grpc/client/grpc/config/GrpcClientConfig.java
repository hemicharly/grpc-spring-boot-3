package com.example.grpc.client.grpc.config;

import com.example.grpc.models.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .idleTimeout(10, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub(final ManagedChannel managedChannel) {
        return CalculatorServiceGrpc.newBlockingStub(managedChannel);
    }

}
