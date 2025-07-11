package com.example.tasteguidebackv2.common.jwt;

import com.example.tasteguidebackv2.domain.users.entity.UserRole;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static com.example.tasteguidebackv2.common.jwt.JwtConstants.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(Long id, UserRole userRole) {
		return buildToken(id, userRole, accessTokenExpiration, false);
	}

	public String createRefreshToken(Long id, UserRole userRole) {
		return buildToken(id, userRole, refreshTokenExpiration, true);
	}

	public UserAuth extractUserAuth(String token) {
		Claims claims = parseClaims(token);
		Long userId = Long.parseLong(claims.getSubject());
		UserRole userRole = UserRole.valueOf(claims.get(CLAIM_USER_ROLE, String.class));
		return new UserAuth(userId, userRole);
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("JWT 만료됨");
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			log.warn("유효하지 않은 JWT: {}", e.getMessage());
		}
		return false;
	}

	public String extractToken(HttpServletRequest request) {
		String bearer = request.getHeader(HEADER_AUTHORIZATION);
		if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
			return bearer.substring(TOKEN_PREFIX.length());
		}
		return null;
	}

	public String extractJti(String token) {
		Claims claims = parseClaims(token);
		return claims.get(CLAIM_JTI, String.class);
	}

	public long getExpiration(String token) {
		Claims claims = parseClaims(token);
		return claims.getExpiration().getTime() - System.currentTimeMillis();
	}

	private String buildToken(Long id, UserRole userRole, long expiration, boolean includeJti) {
		JwtBuilder builder = Jwts.builder()
				.setSubject(String.valueOf(id))
				.claim(CLAIM_USER_ROLE, userRole.name())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration));

		if (includeJti) {
			builder.claim(CLAIM_JTI, UUID.randomUUID().toString());
		}

		return builder
				.signWith(getSigningKey())
				.compact();
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}