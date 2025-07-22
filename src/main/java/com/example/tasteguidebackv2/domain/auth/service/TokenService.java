package com.example.tasteguidebackv2.domain.auth.service;

import com.example.tasteguidebackv2.common.jwt.JwtTokenProvider;
import com.example.tasteguidebackv2.domain.auth.dto.response.TokenResponse;
import com.example.tasteguidebackv2.domain.users.entity.UserRole;
import com.example.tasteguidebackv2.domain.users.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisRepository redisRepository;

	public TokenResponse generateTokens(Long userId, UserRole userRole) {
		String accessToken = jwtTokenProvider.createAccessToken(userId, userRole);
		String refreshToken = jwtTokenProvider.createRefreshToken(userId, userRole);
		return new TokenResponse(accessToken, refreshToken);
	}

	public void saveRefreshToken(Long userId, String refreshToken) {
		long expiration = jwtTokenProvider.getExpiration(refreshToken);
		String jti = jwtTokenProvider.getJti(refreshToken);
		redisRepository.saveRefreshToken(userId, jti, expiration);
	}

	public void deleteRefreshToken(Long userId) {
		redisRepository.deleteRefreshToken(userId);
	}

	public boolean validateRefreshToken(Long userId, String jti) {
		return redisRepository.validateRefreshToken(userId, jti);
	}
}
