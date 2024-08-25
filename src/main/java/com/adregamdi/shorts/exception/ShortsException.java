package com.adregamdi.shorts.exception;

public class ShortsException extends RuntimeException{
    public ShortsException(final String message) {super(message);}

    public static class ShortsNotFoundException extends ShortsException {

        private final static String NOT_FOUND_MESSAGE = "쇼츠가 존재하지 않습니다.";

        public ShortsNotFoundException() {super(NOT_FOUND_MESSAGE);}

        public ShortsNotFoundException(final Object object) {
            super(String.format(NOT_FOUND_MESSAGE + "- request info => %s", object));
        }
    }

    public static class ShortsExistException extends ShortsException {

        private final static String ALREADY_EXIST_MESSAGE = "이미 쇼츠가 존재합니다.";

        public ShortsExistException() {super(ALREADY_EXIST_MESSAGE);}

        public ShortsExistException(final Object object) {
            super(String.format(ALREADY_EXIST_MESSAGE + "- request info => %s", object));
        }
    }
}
