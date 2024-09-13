package com.example.geoIot.Exception;

public class PersonAlreadyExistsException extends RuntimeException{
    public PersonAlreadyExistsException(){
        super("Person already exists");
    }
}
