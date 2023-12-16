package com.example.grpc.server.grpc.interceptor;


import com.example.common.audit.GrpcAuditLog;
import com.example.common.exceptions.GrpcException;
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

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class GrpcServerInterceptor implements ServerInterceptor {

    @Value("${spring.application.name}")
    private String applicationName;
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    @Override
    public <R, S> ServerCall.Listener<R> interceptCall(final ServerCall<R, S> serverCall, final Metadata metadata, final ServerCallHandler<R, S> serverCallHandler) {
        try {
            var traceId = metadata.get(Metadata.Key.of(TRACE_ID, Metadata.ASCII_STRING_MARSHALLER));
            var spanId = metadata.get(Metadata.Key.of(SPAN_ID, Metadata.ASCII_STRING_MARSHALLER));
            MDC.put(TRACE_ID, traceId);
            MDC.put(SPAN_ID, spanId);
            final var grpcAuditLog = GrpcAuditLog.builder().traceId(traceId).spanId(spanId).serviceName(applicationName).method(serverCall.getMethodDescriptor().getFullMethodName()).metadata(metadata).build();
            return new WrapRequest<>(serverCall, metadata, serverCallHandler, grpcAuditLog);
        } finally {
            MDC.clear();
        }
    }

    private static class WrapRequest<R, S> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<R> {
        private final ServerCall<R, S> serverCall;
        private final Metadata metadata;
        private final GrpcAuditLog grpcAuditLog;
        private final Instant startTimestamp;

        protected WrapRequest(final ServerCall<R, S> serverCall, final Metadata metadata, final ServerCallHandler<R, S> serverCallHandler, final GrpcAuditLog grpcAuditLog) {
            super(serverCallHandler.startCall(serverCall, metadata));
            this.serverCall = serverCall;
            this.metadata = metadata;
            this.grpcAuditLog = grpcAuditLog;
            this.startTimestamp = Instant.now();
        }

        @Override
        public void onMessage(R message) {
            MDC.put(TRACE_ID, grpcAuditLog.getTraceId());
            MDC.put(SPAN_ID, grpcAuditLog.getSpanId());
            try {
                if (nonNull(message)) {
                    grpcAuditLog.setRequestBody(builderBody(String.valueOf(message)));
                }
                super.onMessage(message);
            } finally {
                MDC.clear();
            }
        }

        @Override
        public void onHalfClose() {
            MDC.put(TRACE_ID, grpcAuditLog.getTraceId());
            MDC.put(SPAN_ID, grpcAuditLog.getSpanId());
            try {
                grpcAuditLog.setCode(Status.Code.OK);
                super.onHalfClose();
            } catch (final GrpcException e) {
                log.error("Error grpc server: ", e);
                grpcAuditLog.setCode(e.getCode());
                grpcAuditLog.setResponseBody(builderBody(e.getMessage()));
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
        public void onComplete() {
            MDC.put(TRACE_ID, grpcAuditLog.getTraceId());
            MDC.put(SPAN_ID, grpcAuditLog.getSpanId());
            try {
                registerGrpcAuditLog(grpcAuditLog, startTimestamp);
                super.onComplete();
            } finally {
                MDC.clear();
            }
        }
    }


    private static String builderBody(final String stringBody) {
        if (nonNull(stringBody)) {
            return "(" + stringBody.replaceAll("\\r?\\n", " ") + ")";
        }
        return null;
    }

    private static void registerGrpcAuditLog(final GrpcAuditLog grpcAuditLog, final Instant startTimestamp) {
        grpcAuditLog.setDuration(Instant.now().toEpochMilli() - startTimestamp.toEpochMilli());
        log.atLevel(grpcAuditLog.getCode().equals(Status.Code.OK) ? Level.INFO : Level.ERROR).log("{}", grpcAuditLog.toString());
    }
}
