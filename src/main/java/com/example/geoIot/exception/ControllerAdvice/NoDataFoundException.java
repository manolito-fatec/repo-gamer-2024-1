package com.example.geoIot.exception.ControllerAdvice;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String message) {
        super(message);
    }
}
