package com.example.geoIot.exception.ControllerAdvice;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
