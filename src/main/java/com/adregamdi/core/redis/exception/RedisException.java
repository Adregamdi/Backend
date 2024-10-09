package com.adregamdi.core.redis.exception;

public class RedisException extends RuntimeException {
    public RedisException(final String message) {
        super(message);
    }

    public static class RedisConnectionFailureException extends RedisException {

        public RedisConnectionFailureException() {
            super("Redis 연결에 실패했습니다.");
        }
    }

    public static class RedisQueryTimeoutException extends RedisException {
        public RedisQueryTimeoutException() {
            super("Redis 쿼리 실행 시간이 초과되었습니다.");
        }
    }

    public static class RedisSystemException extends RedisException {
        public RedisSystemException() {
            super("Redis 시스템 오류가 발생했습니다.");
        }
    }

    public static class RedisInvalidDataAccessException extends RedisException {
        public RedisInvalidDataAccessException() {
            super("잘못된 Redis 데이터 접근 방식이 사용되었습니다.");
        }
    }

    public static class RedisCommandExecutionException extends RedisException {
        public RedisCommandExecutionException() {
            super("Redis 명령 실행 중 오류가 발생했습니다.");
        }
    }

    public static class RedisKeyNotFoundException extends RedisException {
        public RedisKeyNotFoundException() {
            super("요청한 Redis 키를 찾을 수 없습니다.");
        }
    }

    public static class RedisDataSerializationException extends RedisException {
        public RedisDataSerializationException() {
            super("Redis 데이터 직렬화 또는 역직렬화 중 오류가 발생했습니다.");
        }
    }

    public static class RedisOperationNotSupportedException extends RedisException {
        public RedisOperationNotSupportedException() {
            super("지원되지 않는 Redis 작업입니다.");
        }
    }
}
