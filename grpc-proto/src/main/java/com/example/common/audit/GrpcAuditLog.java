package com.example.common.audit;

import io.grpc.Metadata;
import io.grpc.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GrpcAuditLog {
    private final String datetime = LocalDateTime.now().atZone(ZoneId.of("America/Sao_Paulo")).toLocalDateTime().toString();
    private String serviceName;
    private String traceId;
    private String spanId;
    private String method;
    private Metadata metadata;
    private String requestBody;
    private String responseBody;
    private Status.Code code;
    private Long duration;
}
