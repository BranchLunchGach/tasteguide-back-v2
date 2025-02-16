package com.example.tasteguidebackv2.common.exception;

import com.example.tasteguidebackv2.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BizException.class)
	public ResponseEntity<ApiResponse<?>> handleBizError(BizException exception) {
		return ResponseEntity
				.status(exception.getErrorCode().getStatus())
				.body(ApiResponse.error(exception.getErrorCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleValidationError(MethodArgumentNotValidException exception) {
		return ResponseEntity
				.badRequest()
				.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT_VALUE, exception.getBindingResult()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
		return ResponseEntity
				.badRequest()
				.body(ApiResponse.error(CommonErrorCode.MISSING_PARAMETER));
	}
}
