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

	/**
	 * Generates a new access token and refresh token for the specified user and role.
	 *
	 * @param userId   the unique identifier of the user
	 * @param userRole the role assigned to the user
	 * @return a TokenResponse containing the generated access and refresh tokens
	 */
	public TokenResponse generateTokens(Long userId, UserRole userRole) {
		String accessToken = jwtTokenProvider.createAccessToken(userId, userRole);
		String refreshToken = jwtTokenProvider.createRefreshToken(userId, userRole);
		return new TokenResponse(accessToken, refreshToken);
	}

	/**
	 * Stores the refresh token's unique identifier (JTI) and expiration time in Redis for the specified user.
	 *
	 * @param userId the ID of the user to associate with the refresh token
	 * @param refreshToken the JWT refresh token to extract information from
	 */
	public void saveRefreshToken(Long userId, String refreshToken) {
		long expiration = jwtTokenProvider.getExpiration(refreshToken);
		String jti = jwtTokenProvider.getJti(refreshToken);
		redisRepository.saveRefreshToken(userId, jti, expiration);
	}

	/**
	 * Deletes the refresh token associated with the specified user ID from Redis storage.
	 *
	 * @param userId the unique identifier of the user whose refresh token should be removed
	 */
	public void deleteRefreshToken(Long userId) {
		redisRepository.deleteRefreshToken(userId);
	}

	/**
	 * Checks whether the refresh token with the specified JTI is valid for the given user ID.
	 *
	 * @param userId the ID of the user whose refresh token is being validated
	 * @param jti the unique identifier of the refresh token (JWT ID)
	 * @return true if the refresh token is valid for the user, false otherwise
	 */
	public boolean validateRefreshToken(Long userId, String jti) {
		return redisRepository.validateRefreshToken(userId, jti);
	}
}
