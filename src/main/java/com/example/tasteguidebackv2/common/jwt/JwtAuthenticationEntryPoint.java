package com.example.tasteguidebackv2.common.jwt;

import com.example.tasteguidebackv2.common.exception.CommonErrorCode;
import com.example.tasteguidebackv2.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// JwtAuthenticationEntryPoint가 없으면 인증실패시 시큐리티가 소셜로그인으로 리다이렉트
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> error = ApiResponse.error(CommonErrorCode.UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
