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

    public static class ExistBadwordException extends GlobalException {

        public ExistBadwordException() {
            super("비속어가 존재합니다.");
        }
    }
}
