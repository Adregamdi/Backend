package com.adregamdi.media.exception;

import com.adregamdi.shorts.exception.ShortsException;

public class ImageException extends RuntimeException{
    public ImageException(String message) {
        super(message);
    }
    public ImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class ImageNotFoundException extends ShortsException {

        private final static String NOT_FOUND_MESSAGE = "이미지가 존재하지 않습니다.";

        public ImageNotFoundException() {super(NOT_FOUND_MESSAGE);}

        public ImageNotFoundException(final Object object) {
            super(String.format(NOT_FOUND_MESSAGE + "- request info => %s", object));
        }
    }
    
    public static class InvalidFileNameException extends ImageException {

        public InvalidFileNameException() {super("파일 이름이 유효하지 않습니다.");}

        public InvalidFileNameException(final Object object) {
            super(String.format("파일 이름이 유효하지 않습니다. - request info => %s", object));
        }
    }

    public static class UnSupportedImageTypeException extends ImageException {

        public UnSupportedImageTypeException() { super("지원하는 이미지 파일이 아닙니다."); }

        public UnSupportedImageTypeException(final Object object) {
            super(String.format("지원하는 이미지 파일이 아닙니다. - request info => %s", object));
        }
    }

    public static class InvalidImageLengthException extends ImageException {

        public InvalidImageLengthException(final String message) { super(message); }

        public InvalidImageLengthException(final String message, final Object object) {
            super(String.format(message + "- request info => %s", object));
        }
    }
}

