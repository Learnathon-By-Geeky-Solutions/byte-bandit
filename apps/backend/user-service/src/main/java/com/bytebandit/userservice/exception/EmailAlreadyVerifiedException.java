package com.bytebandit.userservice.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}
