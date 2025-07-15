package com.example.tasteguidebackv2.domain.auth.service;

import com.example.tasteguidebackv2.common.exception.BizException;
import com.example.tasteguidebackv2.common.jwt.JwtBlacklistService;
import com.example.tasteguidebackv2.common.jwt.JwtExtractor;
import com.example.tasteguidebackv2.common.jwt.JwtTokenProvider;
import com.example.tasteguidebackv2.common.jwt.UserAuth;
import com.example.tasteguidebackv2.domain.auth.dto.request.LoginRequest;
import com.example.tasteguidebackv2.domain.auth.dto.response.TokenResponse;
import com.example.tasteguidebackv2.domain.auth.exception.AuthErrorCode;
import com.example.tasteguidebackv2.domain.users.entity.User;
import com.example.tasteguidebackv2.domain.users.exception.UserErrorCode;
import com.example.tasteguidebackv2.domain.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtExtractor jwtExtractor;
	private final JwtBlacklistService jwtBlacklistService;

	/**
	 * Authenticates a user with the provided login credentials and issues new access and refresh tokens.
	 *
	 * If the email does not exist or the password is incorrect, an exception is thrown. On successful authentication, new tokens are generated and the refresh token is stored for future validation.
	 *
	 * @param request the login credentials containing email and password
	 * @return a TokenResponse containing the generated access and refresh tokens
	 * @throws BizException if the email is not found or the password is invalid
	 */
	@Transactional
	public TokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmailOrElseThrow(request.email());

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}

		TokenResponse tokens = tokenService.generateTokens(user.getId(), user.getUserRole());
		tokenService.saveRefreshToken(user.getId(), tokens.getRefreshToken());

		return tokens;
	}

	/**
	 * Logs out the user by invalidating the provided JWT token and deleting the associated refresh token.
	 *
	 * If a valid token is found in the HTTP request, it is added to a blacklist to prevent further use, and the user's refresh token is removed. Any exceptions during this process are logged and suppressed.
	 */
	@Transactional
	public void logout(HttpServletRequest request) {
		try {
			String token = jwtExtractor.extractToken(request);
			if (token != null && jwtTokenProvider.validateToken(token)) {
				long expiration = jwtTokenProvider.getExpiration(token);
				jwtBlacklistService.addToBlacklist(token, expiration);
				UserAuth userAuth = jwtTokenProvider.getUserAuth(token);
				tokenService.deleteRefreshToken(userAuth.getId());
			}
		} catch (Exception e) {
			// 로그만 남기고 조용히 무시
			log.warn("로그아웃 처리 중 예외 발생: {}", e.getMessage());
		}
	}

	/**
	 * Reissues new access and refresh tokens using a valid refresh token.
	 *
	 * Extracts and validates the refresh token from the provided bearer token. If the token is valid and matches the stored refresh token, deletes the old refresh token, generates new tokens, saves the new refresh token, and returns the new tokens. Throws a business exception if the token is invalid, missing, or has been reused.
	 *
	 * @param bearerToken the bearer token string containing the refresh token
	 * @return a new {@link TokenResponse} containing refreshed access and refresh tokens
	 */
	@Transactional
	public TokenResponse reissue(String bearerToken) {
		// 1. Bearer 제거
		String refreshToken = jwtExtractor.extractToken(bearerToken);
		if (refreshToken == null) {
			throw new BizException(AuthErrorCode.INVALID_AUTH_HEADER);
		}

		// 2. 토큰 유효성 검증
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new BizException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 3. 유저 정보 추출
		UserAuth userAuth = jwtTokenProvider.getUserAuth(refreshToken);

		// 4. Redis에 저장된 Refresh Token과 일치하는지 확인
		String jti = jwtTokenProvider.getJti(refreshToken);
		if (!tokenService.validateRefreshToken(userAuth.getId(), jti)) {
			throw new BizException(AuthErrorCode.REUSED_REFRESH_TOKEN);
		}

		tokenService.deleteRefreshToken(userAuth.getId());

		// 5. 새 토큰 생성 및 저장
		TokenResponse newTokens = tokenService.generateTokens(userAuth.getId(), userAuth.getUserRole());
		tokenService.saveRefreshToken(userAuth.getId(), newTokens.getRefreshToken());

		return newTokens;
	}
}
