package com.example.geoIot.exception;

public class PersonAlreadyExistsException extends RuntimeException{
    public PersonAlreadyExistsException(){
        super("Person already exists");
    }
}
