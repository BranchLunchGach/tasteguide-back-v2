package com.example.tasteguidebackv2.domain.auth.service;

import com.example.tasteguidebackv2.common.jwt.JwtUtil;
import com.example.tasteguidebackv2.domain.auth.dto.response.TokenResponse;
import com.example.tasteguidebackv2.domain.users.entity.UserRole;
import com.example.tasteguidebackv2.domain.users.repository.RedisRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtUtil jwtUtil;
	private final RedisRepository redisRepository;

	public TokenResponse generateTokens(Long userId, UserRole userRole) {
		String accessToken = jwtUtil.createToken(userId, userRole);
		String refreshToken = jwtUtil.createRefreshToken(userId, userRole);
		return new TokenResponse(accessToken, refreshToken);
	}

	public void saveRefreshToken(Long userId, String refreshToken) {
		long refreshExpiration = jwtUtil.getRefreshExpiration(refreshToken);
		redisRepository.saveRefreshToken(userId, refreshToken, refreshExpiration);
	}
}
