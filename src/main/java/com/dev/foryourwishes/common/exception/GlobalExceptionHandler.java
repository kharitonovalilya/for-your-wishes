package com.dev.foryourwishes.common.exception;

import com.dev.foryourwishes.user.exception.UserNotFoundException;
import com.dev.foryourwishes.wishlist.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            WishNotFoundException.class,
            WishlistNotFoundException.class,
            ReservationNotFoundException.class,
            UserNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            WishlistAccessDeniedException.class,
            ReservationAccessDeniedException.class
    })
    public ProblemDetail handleAccessDenied(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            WishFulfilledException.class,
            WishReservedException.class,
            WishlistArchivedException.class,
            OwnWishReservationException.class
    })
    public ProblemDetail handleConflict(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

}
