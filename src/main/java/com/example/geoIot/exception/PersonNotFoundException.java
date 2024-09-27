package com.example.geoIot.exception;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException(String name) {
        super("Person not found " + name);
    }

    public PersonNotFoundException() {
        super("Person not found ");
    }
}
