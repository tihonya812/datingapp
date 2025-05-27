package com.tihonya.datingapp.exception;

public class PhotoDeletionException extends RuntimeException {
    public PhotoDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}