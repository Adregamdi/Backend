package com.adregamdi.schedule.exception;

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
}
