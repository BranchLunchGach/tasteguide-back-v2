package com.example.tasteguidebackv2.domain.users.repository;

import com.example.tasteguidebackv2.common.exception.BizException;
import com.example.tasteguidebackv2.domain.users.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

	private final RedisTemplate<String,String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "refresh:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	public boolean validateKey(String token){
		try {
			return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public void saveBlackListToken(String token, long expirationMillis) {
		try {
			redisTemplate.opsForValue().set(
				BLACKLIST_PREFIX + token,
				"logout",
				expirationMillis,
				TimeUnit.MILLISECONDS
			);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	// ✅ 리프레시 토큰의 jti 저장 (토큰 전체 대신 jti만 저장)
	public void saveRefreshToken(Long userId, String jti, long expirationMillis) {
		try {
			String key = REFRESH_TOKEN_PREFIX + userId;
			redisTemplate.opsForValue().set(key, jti, expirationMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	// ✅ 토큰에서 추출한 jti와 비교
	public boolean validateRefreshToken(Long userId, String jti) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		String savedJti = redisTemplate.opsForValue().get(key);
		return Objects.equals(savedJti, jti);
	}

	public void deleteRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.delete(key);
	}
}
