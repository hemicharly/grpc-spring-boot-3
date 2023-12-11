package com.example.grpc.client.grpc.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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
}
