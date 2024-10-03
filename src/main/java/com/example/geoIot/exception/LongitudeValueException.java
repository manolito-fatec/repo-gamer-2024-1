package com.example.geoIot.exception;

public class LongitudeValueException  extends RuntimeException{
    public LongitudeValueException() {
        super("Longitude must be between -180 and 180 degrees");
    }
}
