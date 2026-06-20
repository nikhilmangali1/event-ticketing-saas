package com.nikhil.ticketflow.common.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException{
    public ResourceNotFoundException(String message){
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "message");
    }
}
