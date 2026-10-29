package com.dev.foryourwishes.user.exception;

public class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException(String login) {
        super("User with login %s is already existed".formatted(login));
    }
}
