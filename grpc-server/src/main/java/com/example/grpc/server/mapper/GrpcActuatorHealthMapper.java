package com.example.grpc.server.mapper;

import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import java.util.List;
import java.util.stream.Collectors;

import static io.grpc.Status.UNAVAILABLE;

@Mapper(componentModel = "spring")
public interface GrpcActuatorHealthMapper {
    GrpcActuatorHealthMapper MAPPER = Mappers.getMapper(GrpcActuatorHealthMapper.class);

    default HealthCheckResponse toHealthCheckResponseUP(final HealthCheckResponse.ServingStatus status) {
        return HealthCheckResponse.newBuilder()
                .setStatus(status)
                .build();
    }

    default StatusRuntimeException toHealthCheckResponseError(final List<HealthIndicator> healthIndicatorList) {
        final var result = toHealthIndicatorListResult(healthIndicatorList);
        return UNAVAILABLE.withDescription("Health failures: " + result).asRuntimeException();
    }

    default String toHealthIndicatorListResult(final List<HealthIndicator> healthIndicatorList) {
        return healthIndicatorList.stream()
                .filter(current -> !Status.UP.equals(current.health().getStatus()))
                .map(this::logAndExtractStatus)
                .collect(Collectors.joining());
    }

    default String logAndExtractStatus(final HealthIndicator indicator) {
        final var className = indicator.getClass().getSimpleName();
        return String.format("Health Check %s: %s", className, indicator.health().getStatus());
    }

}
