package com.example.geoIot.exception;

public class LatitudeValueException extends RuntimeException{
    public LatitudeValueException() {
        super("Latitude must be between -90 and 90 degrees");
    }
}
