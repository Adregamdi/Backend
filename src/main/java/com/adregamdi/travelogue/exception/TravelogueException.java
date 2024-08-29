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
}
