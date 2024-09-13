package com.example.geoIot.Exception;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException() {
        super("The provided identifier (ID) is not associated with any person registered in the system.");
    }
}
