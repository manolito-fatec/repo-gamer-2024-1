package com.example.geoIot.exception;

public class EmptyDataListInRedisException extends RuntimeException{
    public EmptyDataListInRedisException(){
        super("Empty data list in redis");
    }
}
