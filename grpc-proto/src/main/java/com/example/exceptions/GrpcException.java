package com.example.exceptions;

public class GrpcException extends BaseException {

	public GrpcException(final int statusCode, final String code, final String message) {
		super(statusCode, code, message);
	}
}
