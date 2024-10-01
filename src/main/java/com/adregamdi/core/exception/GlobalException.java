package com.adregamdi.core.exception;

public class GlobalException extends RuntimeException {

    public GlobalException(final String message) {
        super(message);
    }

    public static class LogoutMemberException extends GlobalException {

        public LogoutMemberException() {
            super("로그아웃 상태인 회원입니다.");
        }
    }

    public static class EmptyTokenException extends GlobalException {
        public EmptyTokenException() {
            super("토큰이 비어있습니다.");
        }
    }

    public static class TokenExpiredException extends GlobalException {
        public TokenExpiredException() {
            super("토큰이 만료되었습니다.");
        }
    }

    public static class TokenValidationException extends GlobalException {
        public TokenValidationException(String message) {
            super(message);
        }
    }

    public static class MalformedTokenException extends GlobalException {
        public MalformedTokenException() {
            super("토큰의 형식이 올바르지 않습니다.");
        }
    }

    public static class UnsupportedTokenException extends GlobalException {
        public UnsupportedTokenException() {
            super("지원하지 않는 형식의 토큰입니다.");
        }
    }

    public static class TokenIssuedAtFutureException extends GlobalException {
        public TokenIssuedAtFutureException() {
            super("토큰의 발행 시간이 현재 시간보다 미래입니다.");
        }
    }

    public static class TokenClaimMissingException extends GlobalException {
        public TokenClaimMissingException(String claimName) {
            super("토큰에 필수 클레임(" + claimName + ")이 누락되었습니다.");
        }
    }

    public static class RefreshTokenMismatchException extends GlobalException {
        public RefreshTokenMismatchException() {
            super("제공된 리프레시 토큰이 저장된 토큰과 일치하지 않습니다.");
        }
    }

    public static class ExistBadwordException extends GlobalException {

        public ExistBadwordException() {
            super("비속어가 존재합니다.");
        }
    }
}
