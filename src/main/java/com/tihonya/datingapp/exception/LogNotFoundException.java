package com.tihonya.datingapp.exception;

public class LogNotFoundException extends RuntimeException {
    public LogNotFoundException(String message) {
        super(message);
    }
}
