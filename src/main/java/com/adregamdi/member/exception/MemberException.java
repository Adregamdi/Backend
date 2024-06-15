package com.adregamdi.member.exception;

public class MemberException extends RuntimeException {

    public MemberException(final String message) {
        super(message);
    }

    public static class MemberNotFoundException extends MemberException {

        public MemberNotFoundException() {
            super("회원이 존재하지 않습니다.");
        }

        public MemberNotFoundException(final Object data) {
            super(String.format("회원이 존재하지 않습니다. - request info => %s", data));
        }
    }

    public static class AllMemberNotFoundException extends MemberException {

        public AllMemberNotFoundException() {
            super("모든 회원이 존재하지 않습니다.");
        }
    }
}