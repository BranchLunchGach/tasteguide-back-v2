package com.example.tasteguidebackv2.common.jwt;

import com.example.tasteguidebackv2.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.example.tasteguidebackv2.common.exception.CommonErrorCode;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    // 권한 부(로그인은 되어있지만 ROLE_ADMIN만 접근 가능한 페이지에 ROLE_USER가 요청한 경우)
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorResponse error = ErrorResponse.of(CommonErrorCode.ACCESS_DENIED);

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
