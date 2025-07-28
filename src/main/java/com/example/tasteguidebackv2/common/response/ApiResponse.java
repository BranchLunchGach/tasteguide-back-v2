package com.example.tasteguidebackv2.common.response;

import com.example.tasteguidebackv2.common.exception.ErrorCode;
import com.example.tasteguidebackv2.common.exception.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 Json응답에서 제외, 성공한 요청에는 errors가 필요 없으니 JSON에서 아예 빠지도록 설정
public class ApiResponse<T> {

    private String status; // "SUCCESS" 또는 "FAIL"
    private String code;   // S200, U001 등
    private String message;
    private T data;        // 성공 시 응답 데이터
    private List<ErrorResponse.FieldError> errors; // 실패 시 필드 오류 목록

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .code("S200")
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> success(String message) {
        return success(message, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return ApiResponse.<Void>builder()
                .status("FAIL")
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(Collections.emptyList())
                .build();
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, BindingResult bindingResult) {
        return ApiResponse.<Void>builder()
                .status("FAIL")
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(ErrorResponse.FieldError.of(bindingResult))
                .build();
    }
}

