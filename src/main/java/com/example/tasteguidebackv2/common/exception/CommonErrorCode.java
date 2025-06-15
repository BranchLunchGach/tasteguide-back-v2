package com.example.tasteguidebackv2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
	LOCK_FAILED(HttpStatus.BAD_REQUEST,"C002","락 획득 실패"),
	LOCK_INTERRUPTED(HttpStatus.BAD_REQUEST,"C003","락이 인터럽트 됐습니다.");

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
