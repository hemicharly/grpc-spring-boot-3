package com.example.common.audit;

import io.grpc.Status;

import java.time.Instant;
import java.util.logging.Logger;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;


public class GrpcAuditLogHelper {

    private static final Logger log = Logger.getLogger(GrpcAuditLogHelper.class.getName());

    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String CALLED_BY = "calledBy";

    public static String builderBody(final String stringBody) {
        if (nonNull(stringBody)) {
            return "( " + stringBody.replaceAll("\\r?\\n", "; ") + " )";
        }
        return null;
    }

    public static void registerGrpcAuditLog(final GrpcAuditLog grpcAuditLog, final Instant startTimestamp) {
        grpcAuditLog.setDuration(Instant.now().toEpochMilli() - startTimestamp.toEpochMilli());
        if (nonNull(grpcAuditLog.getCode()) && grpcAuditLog.getCode().equals(Status.Code.OK)) {
            log.info(format("{0}", grpcAuditLog.toString()));
            return;
        }

        log.severe(format("{0}", grpcAuditLog.toString()));
    }
}
