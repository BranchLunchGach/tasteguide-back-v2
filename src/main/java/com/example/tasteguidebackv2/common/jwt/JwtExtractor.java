package com.example.tasteguidebackv2.common.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import static com.example.tasteguidebackv2.common.jwt.JwtConstants.HEADER_AUTHORIZATION;
import static com.example.tasteguidebackv2.common.jwt.JwtConstants.TOKEN_PREFIX;

@Component
public class JwtExtractor {

    /**
     * HTTP 요청 헤더에서 Bearer 토큰 추출
     */
    public String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(HEADER_AUTHORIZATION);
        if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
            return bearer.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * RefreshToken 요청 헤더에서 Bearer 토큰 추출
     */
    public String extractToken(String bearerHeader) {
        if (bearerHeader != null && bearerHeader.startsWith(TOKEN_PREFIX)) {
            return bearerHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
