package com.example.tasteguidebackv2.common.jwt;

import com.example.tasteguidebackv2.domain.users.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static com.example.tasteguidebackv2.common.jwt.JwtConstants.*;

@Slf4j
@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	/**
	 * Generates and returns the cryptographic signing key derived from the configured secret key.
	 *
	 * @return the HMAC SHA signing key used for JWT operations
	 */
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generates a JWT access token for the specified user ID and role.
	 *
	 * @param userId the unique identifier of the user
	 * @param role the role assigned to the user
	 * @return a signed JWT access token string
	 */
	public String createAccessToken(Long userId, UserRole role) {
		return buildToken(userId, role, accessTokenExpiration, false);
	}

	/**
	 * Creates a refresh token for the specified user, including a JWT ID (JTI) claim.
	 *
	 * @param userId the unique identifier of the user
	 * @param role the user's role to be included in the token claims
	 * @return a signed JWT refresh token string containing the user ID, role, and JTI
	 */
	public String createRefreshToken(Long userId, UserRole role) {
		return buildToken(userId, role, refreshTokenExpiration, true);
	}

	/****
	 * Validates the given JWT token.
	 *
	 * @param token the JWT token to validate
	 * @return true if the token is valid and not expired; false otherwise
	 */
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("JWT 만료됨");
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("유효하지 않은 JWT: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * Extracts user authentication information from the given JWT token.
	 *
	 * Parses the token to retrieve the user ID and user role claims, returning a {@code UserAuth} object containing these values.
	 *
	 * @param token the JWT token to parse
	 * @return a {@code UserAuth} object with the extracted user ID and role
	 */
	public UserAuth getUserAuth(String token) {
		Claims claims = parseClaims(token);
		Long userId = Long.parseLong(claims.getSubject());
		UserRole userRole = UserRole.valueOf(claims.get(CLAIM_USER_ROLE, String.class));
		return new UserAuth(userId, userRole);
	}

	/**
	 * Extracts the JWT ID (JTI) claim from the provided token.
	 *
	 * @param token the JWT token from which to extract the JTI
	 * @return the JTI value, or null if not present in the token
	 */
	public String getJti(String token) {
		return parseClaims(token).get(CLAIM_JTI, String.class);
	}

	/**
	 * Returns the remaining time in milliseconds until the JWT token expires.
	 *
	 * @param token the JWT token to check
	 * @return the number of milliseconds until the token's expiration time
	 */
	public long getExpiration(String token) {
		return parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
	}

	/**
	 * Constructs a JWT token with the specified user ID, role, expiration time, and optional JWT ID (JTI).
	 *
	 * @param userId      the unique identifier of the user to set as the token subject
	 * @param role        the user role to include as a custom claim
	 * @param expiration  the token's validity period in milliseconds
	 * @param includeJti  whether to include a randomly generated JWT ID (JTI) claim
	 * @return the generated JWT token as a compact string
	 */
	private String buildToken(Long userId, UserRole role, long expiration, boolean includeJti) {
		JwtBuilder builder = Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim(CLAIM_USER_ROLE, role.name())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration));

		if (includeJti) {
			builder.claim(CLAIM_JTI, UUID.randomUUID().toString());
		}

		return builder.signWith(getSigningKey()).compact();
	}

	/**
	 * Parses the given JWT token and returns its claims.
	 *
	 * @param token the JWT token to parse
	 * @return the claims contained in the token
	 * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
	 */
	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
