package com.adregamdi.core.handler;

import com.adregamdi.block.exception.BlockException;
import com.adregamdi.core.redis.exception.RedisException;
import com.adregamdi.like.exception.LikesException;
import com.adregamdi.media.exception.ImageException;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.notification.exception.NotificationException;
import com.adregamdi.place.exception.PlaceException;
import com.adregamdi.shorts.exception.ShortsException;
import com.adregamdi.travel.exception.TravelException;
import com.adregamdi.travelogue.exception.TravelogueException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ws.schild.jave.EncoderException;

import java.time.DateTimeException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "관리자에게 문의해 주세요.";
    private static final String ERROR_KEY_FORMAT = "%n error key : %s";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final int ERROR_KEY_LENGTH = 5;
    private static final String EXCEPTION_CLASS_TYPE_MESSAGE_FORMANT = "%n class type : %s";
    private final Random random = new Random();

    // 필수 파라미터 예외
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        log.warn("Required request parameter is missing: {}", exception.getParameterName());
        String errorMessage = String.format("필수 파라미터 '%s'가 누락되었습니다.", exception.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // 파라미터 타입 예외
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Type mismatch for parameter: {}. Required type: {}", exception.getName(),
                exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown");
        String errorMessage;
        if (exception.getRequiredType() != null) {
            errorMessage = String.format("파라미터 '%s'의 형식이 올바르지 않습니다. 예상 타입: %s",
                    exception.getName(), exception.getRequiredType().getSimpleName());
        } else {
            errorMessage = String.format("파라미터 '%s'의 형식이 올바르지 않습니다.", exception.getName());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // @Validated 있는 클래스에서 @RequestParam, @PathVariable 등에 적용된 제약 조건 예외
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException exception) {
        log.warn("Constraint violation: {}", exception.getMessage());

        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "입력값이 유효하지 않습니다: " + errorMessage));
    }

    // @Valid, @Validated 있는 곳에서 주로 @RequestBody dto 필드에 적용된 검증 어노테이션 유효성 검사 실패 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.warn(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    // json 파싱, 날짜/시간 형식 예외
    @ExceptionHandler(value = {
            HttpMessageNotReadableException.class,
            DateTimeException.class
    })
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(final DateTimeException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "DateTime 형식이 잘못되었습니다. 서버 관리자에게 문의해 주세요."));
    }

    // 존재x 예외
    @ExceptionHandler(value = {
            MemberException.MemberNotFoundException.class,
            NotificationException.NotificationNotFoundException.class,
            PlaceException.PlaceNotFoundException.class,
            PlaceException.PlaceReviewNotFoundException.class,
            PlaceException.PlaceReviewImageNotFoundException.class,
            TravelException.TravelNotFoundException.class,
            TravelException.TravelDayNotFoundException.class,
            TravelException.TravelPlaceNotFoundException.class,
            TravelogueException.TravelogueNotFoundException.class,
            TravelogueException.TravelogueImageNotFoundException.class,
            TravelogueException.TravelogueDayNotFoundException.class,
            ShortsException.ShortsNotFoundException.class,
            ImageException.ImageNotFoundException.class,
            LikesException.LikesNotFoundException.class,
            BlockException.BlockNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(final RuntimeException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    // 존재 예외
    @ExceptionHandler(value = {
            MemberException.HandleExistException.class,
            PlaceException.PlaceExistException.class,
            PlaceException.PlaceReviewExistException.class,
            ShortsException.ShortsExistException.class,
            TravelogueException.TravelogueExistException.class,
            BlockException.BlockExistException.class
    })
    public ResponseEntity<ErrorResponse> handleExistException(final RuntimeException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT.value())
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage()));
    }

    // Redis 관련 예외 처리
    @ExceptionHandler(RedisException.class)
    public ResponseEntity<ErrorResponse> handleRedisException(RedisException exception) {
        log.error("Redis 작업 중 오류 발생: {}", exception.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = exception.getMessage();

        if (exception instanceof RedisException.RedisConnectionFailureException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisQueryTimeoutException) {
            status = HttpStatus.REQUEST_TIMEOUT;
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisSystemException) {
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisInvalidDataAccessException) {
            status = HttpStatus.BAD_REQUEST;
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisCommandExecutionException) {
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisKeyNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisDataSerializationException) {
            message = exception.getMessage();
        } else if (exception instanceof RedisException.RedisOperationNotSupportedException) {
            status = HttpStatus.BAD_REQUEST;
            message = exception.getMessage();
        }

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.value(), message));
    }

    // 커스텀 예외
    @ExceptionHandler(value = {
            TravelException.class,
            TravelogueException.TravelNotEndedException.class,
            ShortsException.ShortsNOTWRITERException.class,
            ImageException.UnSupportedImageTypeException.class,
            ImageException.InvalidFileNameException.class,
            ImageException.InvalidImageLengthException.class,
            AmazonS3Exception.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomBadRequestException(final RuntimeException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler(EncoderException.class)
    public ResponseEntity<ErrorResponse> handleEncoderException(final EncoderException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일을 인코딩하는 과정에서 에러가 발생하였습니다. 재업로드 해주세요."));
    }

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
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), DEFAULT_ERROR_MESSAGE + errorKeyInfo));
    }
}
