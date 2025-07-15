package com.example.tasteguidebackv2.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtExtractor jwtExtractor;
	private final JwtBlacklistService jwtBlacklistService;

	/**
	 * Processes incoming HTTP requests to authenticate users based on JWT tokens.
	 *
	 * Extracts a JWT token from the request, checks if it is blacklisted, and validates it. If the token is valid and not blacklisted, sets the authentication in the security context for the current request. If the token is missing, blacklisted, or invalid, the request is either passed through without authentication or an unauthorized error is returned.
	 *
	 * @param request  the incoming HTTP request
	 * @param response the HTTP response
	 * @param filterChain the filter chain to continue processing
	 * @throws ServletException if an error occurs during filtering
	 * @throws IOException if an I/O error occurs during filtering
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {

		String token = jwtExtractor.extractToken(request);

		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 블랙리스트 토큰인지 확인
		if (jwtBlacklistService.isBlacklisted(token)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "이미 로그아웃된 토큰입니다.");
			return;
		}

		try {
			if (jwtTokenProvider.validateToken(token)) {
				UserAuth userAuth = jwtTokenProvider.getUserAuth(token);

				List<SimpleGrantedAuthority> authorities = List.of(
						new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole().name())
				);

				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userAuth, null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 예외 발생", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
