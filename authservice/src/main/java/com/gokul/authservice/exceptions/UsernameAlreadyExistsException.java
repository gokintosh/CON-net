package com.gokul.authservice.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message){
        super(message);
    }
}
