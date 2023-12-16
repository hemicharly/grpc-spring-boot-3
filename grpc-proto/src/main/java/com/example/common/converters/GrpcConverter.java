package com.example.common.converters;

import io.grpc.Status;

public class GrpcConverter {

	public static int grpcCodeToHttpStatusCode(final Status.Code grpcCode) {
		return switch (grpcCode) {
			case OK -> 200;
			case CANCELLED -> 499;
			case UNKNOWN -> 500;
			case INVALID_ARGUMENT -> 400;
			case DEADLINE_EXCEEDED -> 504;
			case NOT_FOUND -> 404;
			case ALREADY_EXISTS -> 409;
			case PERMISSION_DENIED -> 403;
			case RESOURCE_EXHAUSTED -> 429;
			case FAILED_PRECONDITION -> 412;
			case ABORTED -> 409;
			case OUT_OF_RANGE -> 400;
			case UNIMPLEMENTED -> 501;
			case INTERNAL -> 500;
			case UNAVAILABLE -> 503;
			case DATA_LOSS -> 500;
			case UNAUTHENTICATED -> 401;
			default -> 500;
		};
	}

	public static String builderMessageDefault(final int statusCode) {
		return switch (statusCode) {
			case 401 -> "401 Unauthorized";
			case 429 -> "429 Too Many Requests";
			case 499 -> "499 Client Closed Request";
			case 503 -> "503 Service Unavailable";
			case 504 -> "504 Gateway Timeout";
			default -> "500 Internal Server Error";
		};
	}
}
