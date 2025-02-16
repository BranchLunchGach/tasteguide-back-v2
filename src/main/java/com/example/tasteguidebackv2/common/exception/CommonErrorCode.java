package com.example.tasteguidebackv2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C000", "서버 내부 오류입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "C002", "필수 파라미터가 누락되었습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "C003", "요청 형식이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C004", "허용되지 않은 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "C005", "지원하지 않는 미디어 타입입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C006", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C007", "접근 권한이 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
