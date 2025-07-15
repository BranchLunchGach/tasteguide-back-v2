package com.example.tasteguidebackv2.common.jwt;

import com.example.tasteguidebackv2.domain.users.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisRepository redisRepository;

    /**
     * Checks whether the specified JWT token is present in the blacklist.
     *
     * @param token the JWT token to check
     * @return true if the token is blacklisted; false otherwise
     */
    public boolean isBlacklisted(String token) {
        return redisRepository.validateKey(token);
    }

    /**
     * Adds a JWT token to the blacklist with a specified expiration time in milliseconds.
     *
     * @param token the JWT token to blacklist
     * @param expirationMillis the duration in milliseconds until the token is removed from the blacklist
     */
    public void addToBlacklist(String token, long expirationMillis) {
        redisRepository.saveBlackListToken(token, expirationMillis);
    }
}
