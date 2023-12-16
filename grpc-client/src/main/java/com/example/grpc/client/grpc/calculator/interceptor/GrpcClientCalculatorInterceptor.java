package com.example.grpc.client.grpc.calculator.interceptor;

import com.example.common.audit.GrpcAuditLog;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

import static com.example.common.audit.GrpcAuditLogHelper.CALLED_BY;
import static com.example.common.audit.GrpcAuditLogHelper.SPAN_ID;
import static com.example.common.audit.GrpcAuditLogHelper.TRACE_ID;
import static com.example.common.audit.GrpcAuditLogHelper.builderBody;
import static com.example.common.audit.GrpcAuditLogHelper.registerGrpcAuditLog;
import static java.util.Objects.nonNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrpcClientCalculatorInterceptor implements ClientInterceptor {
    private static final String GRPC_CLIENT_NAME = "calculator-client";
    private static final String GRPC_SERVER_NAME = "calculator-server";

    private final Tracer tracer;

    @Override
    public <R, S> ClientCall<R, S> interceptCall(final MethodDescriptor<R, S> methodDescriptor, final CallOptions callOptions, final Channel channel) {


        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<S> responseListener, Metadata metadata) {
                final var startTimestamp = Instant.now();
                final var traceContext = Objects.requireNonNull(tracer.currentSpan()).context();
                var traceId = traceContext.traceId();
                var spanId = traceContext.spanId();
                metadata.put(Metadata.Key.of(TRACE_ID, Metadata.ASCII_STRING_MARSHALLER), traceId);
                metadata.put(Metadata.Key.of(SPAN_ID, Metadata.ASCII_STRING_MARSHALLER), spanId);
                metadata.put(Metadata.Key.of(CALLED_BY, Metadata.ASCII_STRING_MARSHALLER), GRPC_CLIENT_NAME);

                final var grpcAuditLog = GrpcAuditLog.builder().traceId(traceId).spanId(spanId).serviceName(GRPC_SERVER_NAME).calledBy(GRPC_CLIENT_NAME).method(methodDescriptor.getFullMethodName()).metadata(metadata).build();


                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {
                                @Override
                                public void onMessage(S message) {
                                    if (nonNull(message)) {
                                        grpcAuditLog.setResponseBody(builderBody(String.valueOf(message)));
                                    }
                                    super.onMessage(message);
                                }

                                @Override
                                public void onHeaders(Metadata headers) {
                                    if (nonNull(headers)) {
                                        grpcAuditLog.setMetadata(metadata);
                                    }
                                    super.onHeaders(headers);
                                }

                                @Override
                                public void onClose(Status status, Metadata trailers) {
                                    grpcAuditLog.setCode(status.getCode());
                                    if (nonNull(status.getCode()) && !status.getCode().equals(Status.Code.OK) && nonNull(status.getDescription())) {
                                        grpcAuditLog.setResponseBody(builderBody(status.getDescription()));
                                    }
                                    registerGrpcAuditLog(grpcAuditLog, startTimestamp);
                                    super.onClose(status, trailers);
                                }
                            },
                        metadata);
            }
        };
    }
}
