package com.example.geoIot.exception.ControllerAdvice;

public class MethodArgumentTypeException extends RuntimeException {
  public MethodArgumentTypeException(String message) {
    super(message);
  }
}
