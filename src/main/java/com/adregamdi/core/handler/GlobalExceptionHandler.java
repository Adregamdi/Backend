package com.adregamdi.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.DateTimeException;
import java.util.Random;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "관리자에게 문의해 주세요.";
    private static final String DEFAULT_FORMAT_ERROR_MESSAGE = "잘못된 형식입니다.";
    private static final String ERROR_KEY_FORMAT = "%n error key : %s";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final int ERROR_KEY_LENGTH = 5;
    private static final String EXCEPTION_CLASS_TYPE_MESSAGE_FORMANT = "%n class type : %s";
    private final Random random = new Random();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.warn(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(value = {
            HttpMessageNotReadableException.class,
            DateTimeException.class
    })
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(final DateTimeException exception) {
        log.warn(exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "DateTime 형식이 잘못되었습니다. 서버 관리자에게 문의해 주세요."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        log.warn(exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, DEFAULT_FORMAT_ERROR_MESSAGE));
    }

//    // 존재x 예외
//    @ExceptionHandler(value = {})
//    public ResponseEntity<ErrorResponse> handleNotFoundException(final RuntimeException exception) {
//        log.warn(exception.getMessage());
//
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage()));
//    }
//
//    // 존재 예외
//    @ExceptionHandler(value = {})
//    public ResponseEntity<ErrorResponse> handleExistException(final RuntimeException exception) {
//        log.warn(exception.getMessage());
//
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage()));
//    }
//
//    // 커스텀 예외
//    @ExceptionHandler(value = {})
//    public ResponseEntity<ErrorResponse> handleCustomBadRequestException(final RuntimeException exception) {
//        log.warn(exception.getMessage());
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage()));
//    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException exception) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ERROR_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        String errorKeyInfo = String.format(ERROR_KEY_FORMAT, sb);
        String exceptionTypeInfo = String.format(EXCEPTION_CLASS_TYPE_MESSAGE_FORMANT, exception.getClass());
        log.error(exception.getMessage() + errorKeyInfo + exceptionTypeInfo);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_ERROR_MESSAGE + errorKeyInfo));
    }
}
