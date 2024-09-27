package com.adregamdi.block.exception;

public class BlockException extends RuntimeException {
    public BlockException(final String message) {
        super(message);
    }

    public static class BlockNotFoundException extends BlockException {

        public BlockNotFoundException() {
            super("차단이 존재하지 않습니다.");
        }

        public BlockNotFoundException(final Object data) {
            super(String.format("차단이 존재하지 않습니다. - request info => %s", data));
        }
    }

    public static class BlockExistException extends BlockException {

        public BlockExistException() {
            super("이미 존재하는 차단입니다.");
        }

        public BlockExistException(final Object data) {
            super(String.format("이미 존재하는 차단입니다. - request info => %s", data));
        }
    }
}
