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

    public static class PlaceExistException extends PlaceException {
        public PlaceExistException() {
            super("이미 장소가 존재합니다.");
        }

        public PlaceExistException(final Object object) {
            super(String.format("이미 장소가 존재합니다. - request info => %s", object));
        }
    }

    public static class PlaceReviewNotFoundException extends PlaceException {
        public PlaceReviewNotFoundException() {
            super("장소 리뷰가 존재하지 않습니다.");
        }

        public PlaceReviewNotFoundException(final Object object) {
            super(String.format("장소 리뷰가 존재하지 않습니다. - request info => %s", object));
        }
    }

    public static class PlaceReviewExistException extends PlaceException {
        public PlaceReviewExistException() {
            super("이미 해당 장소에 대한 리뷰가 존재합니다.");
        }

        public PlaceReviewExistException(final Object object) {
            super(String.format("이미 해당 장소에 대한 리뷰가 존재합니다. - request info => %s", object));
        }
    }

    public static class PlaceReviewImageNotFoundException extends PlaceException {
        public PlaceReviewImageNotFoundException() {
            super("장소 리뷰 이미지가 존재하지 않습니다.");
        }

        public PlaceReviewImageNotFoundException(final Object object) {
            super(String.format("장소 리뷰 이미지가 존재하지 않습니다. - request info => %s", object));
        }
    }
}
