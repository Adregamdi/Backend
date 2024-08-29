package com.adregamdi.travel.exception;

public class ScheduleException extends RuntimeException {
    public ScheduleException(final String message) {
        super(message);
    }

    public static class ScheduleNotFoundException extends ScheduleException {
        public ScheduleNotFoundException() {
            super("일정이 존재하지 않습니다.");
        }

        public ScheduleNotFoundException(final Object object) {
            super(String.format("일정이 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class SchedulePlaceNotFoundException extends ScheduleException {
        public SchedulePlaceNotFoundException() {
            super("해당 일정에 대한 장소가 존재하지 않습니다.");
        }

        public SchedulePlaceNotFoundException(final Object object) {
            super(String.format("해당 일정에 대한 장소가 존재하지 않습니다. - request info => %s", object));
        }
    }
}
