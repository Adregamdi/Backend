package com.adregamdi.core.redis.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String LOGOUT_ACCESS_TOKEN_PREFIX = "LOGOUT:AT:";
    private final RedisTemplate<String, String> redisTemplate;
    private final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 14; // 14일

    public void saveRefreshToken(final String memberId, final String refreshToken) {
        redisTemplate.opsForValue()
                .set("RT:" + memberId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public String getRefreshToken(final String memberId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + memberId);
    }

    public void deleteRefreshToken(final String memberId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + memberId);
    }

    public void logoutUser(final String memberId, final String accessToken) {
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + memberId;
        String logoutAccessTokenKey = LOGOUT_ACCESS_TOKEN_PREFIX + accessToken;

        // 리프레시 토큰 삭제
        redisTemplate.delete(refreshTokenKey);

        // 로그아웃된 액세스 토큰 등록
        redisTemplate.opsForValue().set(logoutAccessTokenKey, "true", ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public boolean isLoggedOutAccessToken(final String accessToken) {
        String logoutAccessTokenKey = LOGOUT_ACCESS_TOKEN_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(logoutAccessTokenKey));
    }
}
