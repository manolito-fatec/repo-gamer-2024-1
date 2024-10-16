package com.example.geoIot.entity.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private long timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;


    public ErrorResponse(String message, int statusCode, String error, String path) {
        this.timestamp = System.currentTimeMillis();
        this.statusCode = statusCode;
        this.message = message;
        this.error = error;
        this.path = path;
    }
}
