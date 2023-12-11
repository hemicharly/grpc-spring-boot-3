package com.api.bhub.application.web.handler;

import static com.lib.bhub.common.helpers.JsonHelper.writeObjectAsString;
import static java.util.Objects.isNull;

import com.api.bhub.application.web.response.BaseErrorResponse;
import com.api.bhub.core.enums.CodeErrorApi;
import com.api.bhub.core.exceptions.BaseException;
import com.api.bhub.core.exceptions.BusinessException;
import com.api.bhub.core.exceptions.ConflictResourceException;
import com.api.bhub.core.exceptions.InternalServerException;
import com.api.bhub.core.exceptions.RequiredFieldException;
import com.api.bhub.core.exceptions.ResourceNotFoundException;
import com.lib.bhub.common.dto.ErrorInternalLog;
import com.lib.bhub.common.validate.exception.CommonRequiredFieldException;
import com.lib.bhub.idempotent.exception.IdempotentException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	public static final String INTERNAL_ERROR_LOG = "[INTERNAL_ERROR] {}";

	@ExceptionHandler(value = { BusinessException.class, RequiredFieldException.class, CommonRequiredFieldException.class, MissingRequestValueException.class, ConstraintViolationException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ApiResponse(
		responseCode = "400",
		description = "Bad Request",
		content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = { @ExampleObject(name = "REQUIRED_FIELDS", value = "{\"code\":\"SKELETON_SERVICE_0001\",\"message\":\"Request body required fields.\"}") }) }
	)
	ResponseEntity<BaseErrorResponse> handleBadRequestException(final RuntimeException e) {
		return this.builderErrorResponse(e);
	}

	@ExceptionHandler(value = { ResourceNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ApiResponse(
		responseCode = "404",
		description = "Not Found",
		content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = { @ExampleObject(name = "RESOURCE_NOT_FOUND", value = "{\"code\":\"SKELETON_SERVICE_0002\",\"message\":\"Transaction not found.\"}") }) }
	)
	ResponseEntity<BaseErrorResponse> handleNotFoundException(final RuntimeException e) {
		return this.builderErrorResponse(e);
	}

	@ExceptionHandler(value = { ConflictResourceException.class, IdempotentException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	@ApiResponse(
		responseCode = "409",
		description = "Conflict",
		content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = { @ExampleObject(name = "IDEMPOTENT_PROCESSING", value = "{\"code\":\"IDEMPOTENT_PROCESSING\",\"message\":\"Retry request, try again later.\"}") }) }
	)
	ResponseEntity<BaseErrorResponse> handleConflictResourceException(final RuntimeException e) {
		return this.builderErrorResponse(e);
	}

	@ExceptionHandler(value = { BaseException.class, Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ApiResponse(
		responseCode = "500",
		description = "Internal Server Error",
		content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BaseErrorResponse.class), examples = { @ExampleObject(name = "INTERNAL_500", value = "{\"code\":\"INTERNAL_500\",\"message\":\"Internal Server Error.\"}") }) }
	)
	ResponseEntity<BaseErrorResponse> handleException(final RuntimeException e) {
		return this.builderErrorResponse(e);
	}

	private ResponseEntity<BaseErrorResponse> builderErrorResponse(final RuntimeException e) {
		if (e instanceof final MissingRequestValueException exception) {
			return this.builderMissingRequestValueError(exception);
		}

		if (e instanceof final ConstraintViolationException exception) {
			return this.builderRequestParamsConstraintsError(exception);
		}

		if (e instanceof final ServerWebInputException exception) {
			return this.builderServerWebInputExceptionError(exception);
		}

		if (e instanceof final CommonRequiredFieldException exception) {
			return this.builderCommonRequiredFieldExceptionError(exception);
		}

		if (e instanceof final IdempotentException exception) {
			return this.builderIdempotentExceptionError(exception);
		}

		if (!(e instanceof final BaseException exception)) {
			return this.builderInternalServerError(e);
		}

		if (e instanceof InternalServerException) {
			return this.builderInternalServerError(e);
		}

		if (isNull(exception.getCode())) {
			return this.builderInternalServerError(e);
		}

		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(exception.getStatusCode(), exception.getCode(), exception.getMessage())).orElse(null), exception);

		return ResponseEntity.status(exception.getStatusCode()).body(new BaseErrorResponse(exception.getCode(), exception.getMessage()));
	}

	private ResponseEntity<BaseErrorResponse> builderInternalServerError(final RuntimeException e) {
		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(500, "INTERNAL_500", e.getMessage())).orElse(null), e);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseErrorResponse("INTERNAL_500", "Internal Server Error"));
	}

	private ResponseEntity<BaseErrorResponse> builderMissingRequestValueError(final MissingRequestValueException e) {
		var statusCode = e.getStatusCode();
		var code = CodeErrorApi.REQUIRED_FIELDS.getCode();
		var reason = e.getReason();

		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(statusCode.value(), code, reason)).orElse(null), e);

		return ResponseEntity.status(e.getStatusCode()).body(new BaseErrorResponse(code, reason));
	}

	private ResponseEntity<BaseErrorResponse> builderRequestParamsConstraintsError(final ConstraintViolationException e) {
		var code = CodeErrorApi.REQUIRED_FIELDS.getCode();
		var reason = e.getMessage();

		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(400, code, reason)).orElse(null), e);

		return ResponseEntity.status(400).body(new BaseErrorResponse(code, reason));
	}

	private ResponseEntity<BaseErrorResponse> builderServerWebInputExceptionError(final ServerWebInputException e) {
		var code = CodeErrorApi.REQUIRED_FIELDS.getCode();
		var message = e.getCause().getMessage();

		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(400, code, message)).orElse(null), e);

		return ResponseEntity.status(400).body(new BaseErrorResponse(code, message));
	}

	private ResponseEntity<BaseErrorResponse> builderIdempotentExceptionError(final IdempotentException e) {
		var statusCode = e.getStatusCode();
		var code = e.getCode();
		var message = e.getMessage();
		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(statusCode, code, message)).orElse(null), e);
		return ResponseEntity.status(statusCode).body(new BaseErrorResponse(code, message));
	}

	private ResponseEntity<BaseErrorResponse> builderCommonRequiredFieldExceptionError(final CommonRequiredFieldException e) {
		var code = CodeErrorApi.REQUIRED_FIELDS.getCode();
		var message = e.getMessage();

		log.error(INTERNAL_ERROR_LOG, writeObjectAsString(new ErrorInternalLog(400, code, message)).orElse(null), e);

		return ResponseEntity.status(400).body(new BaseErrorResponse(code, message));
	}
}
