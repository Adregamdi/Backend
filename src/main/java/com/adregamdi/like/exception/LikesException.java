package com.adregamdi.like.exception;

public class LikesException extends RuntimeException{
    public LikesException(final String message) {super(message);}

    public static class LikesNotFoundException extends LikesException {

        private final static String NOT_FOUND_MESSAGE = "좋아요가 존재하지 않습니다.";

        public LikesNotFoundException() {super(NOT_FOUND_MESSAGE);}

        public LikesNotFoundException(final Object object) {
            super(String.format(NOT_FOUND_MESSAGE + "- request info => %s", object));
        }
    }
}