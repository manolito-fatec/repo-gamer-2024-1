package com.example.geoIot.exception.ControllerAdvice;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
