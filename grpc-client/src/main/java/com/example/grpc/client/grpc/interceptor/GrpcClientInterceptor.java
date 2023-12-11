package com.example.grpc.client.grpc.config.interceptor;

import com.api.bhub.core.exceptions.BhubMicroserviceIntegrationException;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
public class GrpcClientInterceptor implements ClientInterceptor {

	private final Tracer tracer;

	@Override
	public <R, S> ClientCall<R, S> interceptCall(final MethodDescriptor<R, S> methodDescriptor, final CallOptions callOptions, final Channel channel) {
		return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {
			@Override
			public void start(Listener<S> responseListener, Metadata metadata) {
				final var traceContext = Objects.requireNonNull(tracer.currentSpan()).context();
				metadata.put(Metadata.Key.of("traceId", Metadata.ASCII_STRING_MARSHALLER), traceContext.traceId());
				metadata.put(Metadata.Key.of("spanId", Metadata.ASCII_STRING_MARSHALLER), traceContext.spanId());
				super.start(responseListener, metadata);
			}
		};
	}

	public static BhubMicroserviceIntegrationException cardMsGrpcException(final Exception e) {
		log.error("Error card-ms grp handler: ", e);

		final var status = Status.fromThrowable(e);
		if (isNull(status) || isNull(status.getDescription())) {
			return new BhubMicroserviceIntegrationException(500, "INTERNAL_ERROR", e.getMessage());
		}

		final var errorMessage = status.getDescription().split("\n");
		final var code = errorMessage.length == 2 ? errorMessage[1] : "INTERNAL_ERROR";
		final var message = errorMessage.length == 2 ? errorMessage[0] : status.getDescription();

		return new BhubMicroserviceIntegrationException(status.getCode().value(), code, message);
	}
}
