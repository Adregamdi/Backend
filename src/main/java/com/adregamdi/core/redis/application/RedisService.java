package com.adregamdi.core.redis.application;

import com.adregamdi.core.redis.exception.RedisException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String LOGOUT_ACCESS_TOKEN_PREFIX = "LOGOUT:AT:";
    private final RedisTemplate<String, String> redisTemplate;
    private final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 14; // 14일

    public void saveRefreshToken(final String memberId, final String refreshToken) {
        try {
            redisTemplate.opsForValue()
                    .set(REFRESH_TOKEN_PREFIX + memberId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (RedisConnectionFailureException e) {
            log.error("리프레시 토큰 저장 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (RedisException e) {
            log.error("리프레시 토큰 저장 중 Redis 오류", e);
            throw new RedisException.RedisSystemException();
        } catch (Exception e) {
            log.error("리프레시 토큰 저장 중 예기치 않은 오류", e);
            throw new RedisException.RedisCommandExecutionException();
        }
    }

    public String getRefreshToken(final String memberId) {
        try {
            String token = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + memberId);
            if (token == null) {
                throw new RedisException.RedisKeyNotFoundException();
            }
            return token;
        } catch (RedisConnectionFailureException e) {
            log.error("리프레시 토큰 조회 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (SerializationException e) {
            log.error("리프레시 토큰 역직렬화 중 오류", e);
            throw new RedisException.RedisDataSerializationException();
        } catch (RedisException e) {
            log.error("리프레시 토큰 조회 중 Redis 오류", e);
            throw new RedisException.RedisSystemException();
        }
    }

    public String getMemberIdByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }

        String result = null;
        ScanOptions scanOptions = ScanOptions.scanOptions().match(REFRESH_TOKEN_PREFIX + "*").build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                String storedToken = redisTemplate.opsForValue().get(key);
                if (refreshToken.equals(storedToken)) {
                    result = key.substring(REFRESH_TOKEN_PREFIX.length());
                    break;
                }
            }
        } catch (RedisConnectionFailureException e) {
            log.error("RefreshToken으로 MemberId 조회 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (QueryTimeoutException e) {
            log.error("RefreshToken으로 MemberId 조회 중 Redis 쿼리 시간 초과", e);
            throw new RedisException.RedisQueryTimeoutException();
        } catch (RedisSystemException e) {
            log.error("RefreshToken으로 MemberId 조회 중 Redis 시스템 오류", e);
            throw new RedisException.RedisSystemException();
        } catch (InvalidDataAccessApiUsageException e) {
            log.error("RefreshToken으로 MemberId 조회 중 잘못된 Redis API 사용", e);
            throw new RedisException.RedisInvalidDataAccessException();
        } catch (UnsupportedOperationException e) {
            log.error("RefreshToken으로 MemberId 조회 중 지원되지 않는 작업", e);
            throw new RedisException.RedisOperationNotSupportedException();
        } catch (Exception e) {
            log.error("RefreshToken으로 MemberId 조회 중 예기치 않은 오류 발생", e);
            throw new RedisException.RedisCommandExecutionException();
        }
        return result;
    }

    public void deleteRefreshToken(final String memberId) {
        try {
            Boolean deleted = redisTemplate.delete(REFRESH_TOKEN_PREFIX + memberId);
            if (Boolean.FALSE.equals(deleted)) {
                throw new RedisException.RedisKeyNotFoundException();
            }
        } catch (RedisConnectionFailureException e) {
            log.error("리프레시 토큰 삭제 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (RedisException e) {
            log.error("리프레시 토큰 삭제 중 Redis 오류", e);
            throw new RedisException.RedisSystemException();
        } catch (Exception e) {
            log.error("리프레시 토큰 삭제 중 예기치 않은 오류", e);
            throw new RedisException.RedisCommandExecutionException();
        }
    }

    public void logoutUser(final String memberId, final String accessToken) {
        try {
            String logoutAccessTokenKey = LOGOUT_ACCESS_TOKEN_PREFIX + accessToken;

            // 리프레시 토큰 삭제
            deleteRefreshToken(memberId);

            // 로그아웃된 액세스 토큰 등록
            redisTemplate.opsForValue().set(logoutAccessTokenKey, "true", ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (RedisConnectionFailureException e) {
            log.error("로그아웃 처리 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (RedisException e) {
            log.error("로그아웃 처리 중 Redis 오류", e);
            throw new RedisException.RedisSystemException();
        } catch (Exception e) {
            log.error("로그아웃 처리 중 예기치 않은 오류", e);
            throw new RedisException.RedisCommandExecutionException();
        }
    }

    public boolean isLoggedOutAccessToken(final String accessToken) {
        try {
            String logoutAccessTokenKey = LOGOUT_ACCESS_TOKEN_PREFIX + accessToken;
            return Boolean.TRUE.equals(redisTemplate.hasKey(logoutAccessTokenKey));
        } catch (RedisConnectionFailureException e) {
            log.error("로그아웃된 액세스 토큰 확인 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (RedisException e) {
            log.error("로그아웃된 액세스 토큰 확인 중 Redis 오류", e);
            throw new RedisException.RedisSystemException();
        } catch (Exception e) {
            log.error("로그아웃된 액세스 토큰 확인 중 예기치 않은 오류", e);
            throw new RedisException.RedisCommandExecutionException();
        }
    }
}
