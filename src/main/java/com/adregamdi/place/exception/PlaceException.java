package com.adregamdi.place.exception;

public class PlaceException extends RuntimeException {
    public PlaceException(final String message) {
        super(message);
    }

    public static class PlaceNotFoundException extends PlaceException {

        public PlaceNotFoundException() {
            super("장소가 존재하지 않습니다.");
        }

        public PlaceNotFoundException(final Object object) {
            super(String.format("장소가 존재하지 않습니다. - request info => %s", object));
        }
    }
}
