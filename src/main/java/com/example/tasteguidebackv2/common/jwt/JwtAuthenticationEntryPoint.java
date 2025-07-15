package com.example.tasteguidebackv2.common.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// JwtAuthenticationEntryPoint가 없으면 인증실패시 시큐리티가 소셜로그인으로 리다이렉트
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 인증 실패 시 401 상태코드와 메시지 응답
        // 에러메세지 공통처리 필요
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("""
            {
              "status": 401,
              "message": "인증이 필요합니다."
            }
        """);
    }
}
