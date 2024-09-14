package com.example.geoIot.Exception;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException(String name) {
        super("Person not found " + name);
    }

    public PersonNotFoundException() {
        super("Person not found ");
    }
}
