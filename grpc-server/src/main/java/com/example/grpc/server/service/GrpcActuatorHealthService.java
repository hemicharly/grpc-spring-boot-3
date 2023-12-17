package com.example.grpc.server.service;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.grpc.server.mapper.GrpcActuatorHealthMapper.MAPPER;

@Service
@RequiredArgsConstructor
public class GrpcActuatorHealthService extends HealthGrpc.HealthImplBase {

    private final HealthEndpoint healthEndpoint;
    private final List<HealthIndicator> healthIndicatorList;

    @Override
    public void check(final HealthCheckRequest request, final StreamObserver<HealthCheckResponse> responseObserver) {
        final var status = healthEndpoint.health().getStatus();
        if (status.equals(Status.UP)) {
            responseObserver.onNext(MAPPER.toHealthCheckResponseUP(HealthCheckResponse.ServingStatus.SERVING));
            responseObserver.onCompleted();
        }

        if (!status.equals(Status.UP)) {
            responseObserver.onError(MAPPER.toHealthCheckResponseError(healthIndicatorList));
        }
    }
}
