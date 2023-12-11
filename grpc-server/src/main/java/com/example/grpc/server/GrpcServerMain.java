package com.example.grpc.server;

import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class GrpcServerMain {

    public static void main(final String[] args) {
        SpringApplication.run(GrpcServerMain.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final Server server) {
        log.info("GRPC server started on port {}", server.getPort());
        return args -> server.awaitTermination();
    }
}
