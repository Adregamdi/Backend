package com.adregamdi.travel.exception;

public class TravelException extends RuntimeException {
    public TravelException(final String message) {
        super(message);
    }

    public static class TravelNotFoundException extends TravelException {
        public TravelNotFoundException() {
            super("일정이 존재하지 않습니다.");
        }

        public TravelNotFoundException(final Object object) {
            super(String.format("일정이 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class TravelPlaceNotFoundException extends TravelException {
        public TravelPlaceNotFoundException() {
            super("해당 일정에 대한 장소가 존재하지 않습니다.");
        }

        public TravelPlaceNotFoundException(final Object object) {
            super(String.format("해당 일정에 대한 장소가 존재하지 않습니다. - request info => %s", object));
        }
    }
}
