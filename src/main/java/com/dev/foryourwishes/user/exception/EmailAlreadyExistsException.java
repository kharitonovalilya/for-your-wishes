package com.dev.foryourwishes.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("User with email %s is already existed".formatted(email));
    }
}
