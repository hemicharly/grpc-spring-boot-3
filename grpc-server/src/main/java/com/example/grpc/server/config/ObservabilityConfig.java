package com.example.grpc.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Hooks;

@Configuration
@Slf4j
public class ObservabilityConfig {

	@EventListener(ApplicationStartedEvent.class)
	public void onStart() {
		Hooks.enableAutomaticContextPropagation();
	}
}
