package com.adregamdi.travelogue.exception;

import com.adregamdi.travel.exception.TravelException;

public class TravelogueException extends RuntimeException {
    public TravelogueException(final String message) {
        super(message);
    }

    public static class TravelogueNotFoundException extends TravelException {
        public TravelogueNotFoundException() {
            super("여행기가 존재하지 않습니다.");
        }

        public TravelogueNotFoundException(final Object object) {
            super(String.format("여행기가 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class TravelogueExistException extends TravelException {
        public TravelogueExistException() {
            super("이미 여행기가 존재합니다.");
        }

        public TravelogueExistException(final Object object) {
            super(String.format("이미 여행기가 존재합니다. - request info => %s", object));
        }
    }

    public static class TravelogueImageNotFoundException extends TravelogueException {
        public TravelogueImageNotFoundException() {
            super("여행기 이미지가 존재하지 않습니다.");
        }

        public TravelogueImageNotFoundException(final Object object) {
            super(String.format("여행기 이미지가 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class TravelogueDayNotFoundException extends TravelogueException {
        public TravelogueDayNotFoundException() {
            super("날짜 별 여행기가 존재하지 않습니다.");
        }

        public TravelogueDayNotFoundException(final Object object) {
            super(String.format("날짜 별 여행기가 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class TravelNotEndedException extends TravelogueException {
        public TravelNotEndedException() {
            super("여행이 아직 종료되지 않았습니다. 여행기를 작성할 수 없습니다.");
        }

        public TravelNotEndedException(final Object object) {
            super(String.format("여행이 아직 종료되지 않았습니다. 여행기를 작성할 수 없습니다. - request info => %s", object));
        }
    }
}
