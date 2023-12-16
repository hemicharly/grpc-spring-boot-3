package com.example.common.interceptors;


import com.example.common.audit.GrpcAuditLog;
import com.example.common.exceptions.GrpcException;
import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.Instant;

import static com.example.common.audit.GrpcAuditLogHelper.CALLED_BY;
import static com.example.common.audit.GrpcAuditLogHelper.SPAN_ID;
import static com.example.common.audit.GrpcAuditLogHelper.TRACE_ID;
import static com.example.common.audit.GrpcAuditLogHelper.builderBody;
import static com.example.common.audit.GrpcAuditLogHelper.registerGrpcAuditLog;
import static java.util.Objects.nonNull;

@Slf4j
public class GrpcServerInterceptor implements ServerInterceptor {

	private final String applicationName;

	public GrpcServerInterceptor(final String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public <R, S> ServerCall.Listener<R> interceptCall(final ServerCall<R, S> serverCall, final Metadata metadata, final ServerCallHandler<R, S> serverCallHandler) {
		try {
			final var traceId = metadata.get(Metadata.Key.of(TRACE_ID, Metadata.ASCII_STRING_MARSHALLER));
			final var spanId = metadata.get(Metadata.Key.of(SPAN_ID, Metadata.ASCII_STRING_MARSHALLER));
			final var calledBy = metadata.get(Metadata.Key.of(CALLED_BY, Metadata.ASCII_STRING_MARSHALLER));
			MDC.put(TRACE_ID, traceId);
			MDC.put(SPAN_ID, spanId);

			final var grpcAuditLog = GrpcAuditLog.builder().traceId(traceId).spanId(spanId).serviceName(applicationName).calledBy(calledBy).method(serverCall.getMethodDescriptor().getFullMethodName()).metadata(metadata).build();

			return new WrapperAuditLog<>(serverCall, metadata, serverCallHandler, grpcAuditLog);
		} finally {
			MDC.clear();
		}
	}

	private static class WrapperAuditLog<R, S> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<R> {

		private final ServerCall<R, S> serverCall;
		private final Metadata metadata;
		private final GrpcAuditLog grpcAuditLog;
		private final Instant startTimestamp;

		protected WrapperAuditLog(final ServerCall<R, S> serverCall, final Metadata metadata, final ServerCallHandler<R, S> serverCallHandler, final GrpcAuditLog grpcAuditLog) {
			super(serverCallHandler.startCall(new WrapperListener<>(serverCall, grpcAuditLog), metadata));
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

	private static class WrapperListener<R, S> extends ForwardingServerCall.SimpleForwardingServerCall<R, S> {

		private final GrpcAuditLog grpcAuditLog;

		protected WrapperListener(final ServerCall<R, S> serverCall, final GrpcAuditLog grpcAuditLog) {
			super(serverCall);
			this.grpcAuditLog = grpcAuditLog;
		}

		@Override
		public void sendMessage(S message) {
			if (nonNull(message)) {
				grpcAuditLog.setResponseBody(builderBody(String.valueOf(message)));
			}
			super.sendMessage(message);
		}
	}
}
