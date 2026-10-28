package com.dev.foryourwishes.common.exception;

import com.dev.foryourwishes.user.exception.UserNotFoundException;
import com.dev.foryourwishes.wishlist.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.security.sasl.AuthenticationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            WishNotFoundException.class,
            WishlistNotFoundException.class,
            ReservationNotFoundException.class,
            UserNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        problem.setTitle("Resource not found");
        return problem;
    }

    @ExceptionHandler({
            WishlistAccessDeniedException.class,
            ReservationAccessDeniedException.class
    })
    public ProblemDetail handleAccessDenied(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                exception.getMessage()
        );
        problem.setTitle("Access denied");
        return problem;
    }

    @ExceptionHandler({
            WishFulfilledException.class,
            WishReservedException.class,
            WishlistArchivedException.class,
            OwnWishReservationException.class
    })
    public ProblemDetail handleConflict(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
        problem.setTitle("Conflict");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        Map<String, String> properties = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(
                                error.getDefaultMessage(),
                                "Invalid value"
                        ),
                        (first, second) -> first
                ));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request contains invalid fields"
        );
        problem.setTitle("Validation failed");
        problem.setProperty("errors", properties);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request contains invalid data"
        );
        problem.setTitle("Invalid data");
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value %s for parameter %s".formatted(exception.getValue(), exception.getName())
        );
        problem.setTitle("Invalid parameter");
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Invalid login or password"
        );
        problem.setTitle("Authentication failed");
        return problem;
    }

}
