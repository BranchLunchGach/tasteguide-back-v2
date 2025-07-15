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
