package com.adregamdi.travel.exception;

public class TravelException extends RuntimeException {
    public TravelException(final String message) {
        super(message);
    }

    public static class TravelNotFoundException extends TravelException {
        public TravelNotFoundException() {
            super("해당 회원의 일정이 존재하지 않습니다.");
        }

        public TravelNotFoundException(final Object object) {
            super(String.format("해당 회원의 일정이 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class TravelDayNotFoundException extends TravelException {
        public TravelDayNotFoundException() {
            super("해당 일정이 존재하지 않습니다.");
        }

        public TravelDayNotFoundException(final Object object) {
            super(String.format("해당 일정이 존재하지 않습니다. - request info => %s", object));
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

    public static class InvalidTravelDateException extends TravelException {
        public InvalidTravelDateException() {
            super("여행 시작일은 종료일보다 이전이거나 같아야 합니다.");
        }

        public InvalidTravelDateException(final Object object) {
            super(String.format("여행 시작일은 종료일보다 이전이거나 같아야 합니다. - request info => %s", object));
        }
    }

    public static class InvalidTravelDayException extends TravelException {
        public InvalidTravelDayException(int day) {
            super(String.format("일자 %d는 여행 기간을 벗어났습니다.", day));
        }

        public InvalidTravelDayException(int day, Object object) {
            super(String.format("일자 %d는 여행 기간을 벗어났습니다. - request info => %s", day, object));
        }
    }
}
