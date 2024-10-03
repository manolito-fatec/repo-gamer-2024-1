package com.example.geoIot.exception.ControllerAdvice;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
