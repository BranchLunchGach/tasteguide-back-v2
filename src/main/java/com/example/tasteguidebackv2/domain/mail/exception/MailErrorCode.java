package com.example.tasteguidebackv2.domain.mail.exception;

import com.example.tasteguidebackv2.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {
    SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "인증번호 발송에 실패했습니다."),
    EMAIL_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "E002", "이메일 인증 코드가 존재하지 않습니다. 인증번호 발송을 요청해주세요."),
    CODE_NOT_MATCHED(HttpStatus.BAD_REQUEST, "E003", "이메일 인증 코드가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "E004", "이메일 인증이 완료되지 않았습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
