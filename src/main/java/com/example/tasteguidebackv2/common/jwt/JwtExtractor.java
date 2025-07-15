package com.example.tasteguidebackv2.common.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import static com.example.tasteguidebackv2.common.jwt.JwtConstants.HEADER_AUTHORIZATION;
import static com.example.tasteguidebackv2.common.jwt.JwtConstants.TOKEN_PREFIX;

@Component
public class JwtExtractor {

    /**
     * Extracts the JWT bearer token from the Authorization header of the given HTTP request.
     *
     * @param request the HTTP request containing the Authorization header
     * @return the JWT token if present and properly prefixed; otherwise, null
     */
    public String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(HEADER_AUTHORIZATION);
        if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
            return bearer.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * Extracts the JWT token from a bearer authorization header string.
     *
     * @param bearerHeader the raw authorization header value, expected to start with the token prefix (e.g., "Bearer ")
     * @return the JWT token if the header is valid and contains the prefix; otherwise, null
     */
    public String extractToken(String bearerHeader) {
        if (bearerHeader != null && bearerHeader.startsWith(TOKEN_PREFIX)) {
            return bearerHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
