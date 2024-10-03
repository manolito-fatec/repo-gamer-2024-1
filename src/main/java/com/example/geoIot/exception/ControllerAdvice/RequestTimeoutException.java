package com.example.geoIot.exception.ControllerAdvice;

public class RequestTimeoutException extends RuntimeException {
    public RequestTimeoutException(String message) {
        super(message);
    }
}
