package com.example.grpc.client.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private int port;

    @Bean
    public OpenAPI setupOpenAPI() {
        final var info = new Info().title(String.format("%s Management API", applicationName.toUpperCase())).version("1.0").description(String.format("This API exposes endpoints to manage %s.", applicationName));
        return new OpenAPI().info(info).servers(List.of(builderServer()));
    }

    private Server builderServer() {
        final var server = new Server();
        server.setUrl("http://localhost:" + port);
        server.setDescription(String.format("Server URL in %s environment", "Development"));
        return server;
    }
}
