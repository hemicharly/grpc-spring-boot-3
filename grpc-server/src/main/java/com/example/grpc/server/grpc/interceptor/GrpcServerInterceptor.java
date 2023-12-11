package com.example.grpc.server.grpc.interceptor;


import com.example.audit.GrpcAuditLog;
import com.example.exceptions.GrpcException;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class GrpcServerInterceptor implements ServerInterceptor {

    @Value("${spring.application.name}")
    private String applicationName;
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    @Override
    public <R, S> ServerCall.Listener<R> interceptCall(final ServerCall<R, S> serverCall, final Metadata metadata, final ServerCallHandler<R, S> serverCallHandler) {
        final var startTimestamp = Instant.now();
        final var listener = serverCallHandler.startCall(serverCall, metadata);
        var traceId = metadata.get(Metadata.Key.of(TRACE_ID, Metadata.ASCII_STRING_MARSHALLER));
        var spanId = metadata.get(Metadata.Key.of(SPAN_ID, Metadata.ASCII_STRING_MARSHALLER));

        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);

        final var grpcAuditLog = GrpcAuditLog.builder().traceId(traceId).spanId(spanId).serviceName(applicationName).method(serverCall.getMethodDescriptor().getFullMethodName()).metadata(metadata).build();

        try {
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
                @Override
                public void onMessage(R message) {
                    MDC.put(TRACE_ID, traceId);
                    MDC.put(SPAN_ID, spanId);
                    try {
                        grpcAuditLog.setRequestBody(String.valueOf(message));
                        super.onMessage(message);
                    } finally {
                        MDC.clear();
                    }
                }

                @Override
                public void onHalfClose() {
                    MDC.put(TRACE_ID, traceId);
                    MDC.put(SPAN_ID, spanId);
                    try {
                        grpcAuditLog.setCode(Status.Code.OK);
                        super.onHalfClose();
                    } catch (final GrpcException e) {
                        log.error("Error grpc server: ", e);
                        grpcAuditLog.setCode(e.getCode());
                        grpcAuditLog.setResponseBody(e.getMessage());
                        final var status = Status.fromCode(e.getCode()).withDescription(e.getMessage()).augmentDescription(e.getCodeMessage()).withCause(e.getCause());
                        serverCall.close(status, metadata);
                    } catch (final Exception e) {
                        log.error("Error grpc server: ", e);
                        grpcAuditLog.setCode(Status.INTERNAL.getCode());
                        grpcAuditLog.setResponseBody(e.getMessage());
                        final var status = Status.INTERNAL.withDescription(e.getMessage()).augmentDescription("INTERNAL_500").withCause(e.getCause());
                        serverCall.close(status, metadata);
                    } finally {
                        MDC.clear();
                    }
                }

                @Override
                public void onCancel() {
                    MDC.put(TRACE_ID, traceId);
                    MDC.put(SPAN_ID, spanId);
                    try {
                        super.onCancel();
                    } finally {
                        MDC.clear();
                    }
                }

                @Override
                public void onComplete() {
                    MDC.put(TRACE_ID, traceId);
                    MDC.put(SPAN_ID, spanId);
                    try {
                        registerGrpcAuditLog(grpcAuditLog, startTimestamp);
                        super.onComplete();
                    } finally {
                        MDC.clear();
                    }
                }

                @Override
                public void onReady() {
                    MDC.put(TRACE_ID, traceId);
                    MDC.put(SPAN_ID, spanId);
                    try {
                        super.onReady();
                    } finally {
                        MDC.clear();
                    }
                }
            };
        } finally {
            MDC.clear();
        }
    }

    private static void registerGrpcAuditLog(final GrpcAuditLog grpcAuditLog, final Instant startTimestamp) {
        grpcAuditLog.setDuration(Instant.now().toEpochMilli() - startTimestamp.toEpochMilli());
        log.atLevel(grpcAuditLog.getCode().equals(Status.Code.OK) ? Level.INFO : Level.ERROR).log("{}", grpcAuditLog.toString());
    }
}
