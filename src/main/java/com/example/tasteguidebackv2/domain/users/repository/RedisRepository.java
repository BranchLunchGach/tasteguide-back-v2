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

	public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
		try {
			String key = REFRESH_TOKEN_PREFIX + userId;
			System.out.println("저장 시도 key: " + key);
			System.out.println("refreshToken: " + refreshToken);
			System.out.println("expirationMillis: " + expirationMillis);
			System.out.println("redisTemplate: " + redisTemplate);

			redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public boolean validateRefreshToken(Long userId, String refreshToken) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		String savedToken = redisTemplate.opsForValue().get(key);
		return Objects.equals(savedToken, refreshToken);
	}

	public void deleteRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.delete(key);
	}
}
