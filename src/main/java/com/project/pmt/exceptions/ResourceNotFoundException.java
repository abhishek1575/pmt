package com.project.pmt.exceptions;


public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String user, String username, String username1) {
    }

    public ResourceNotFoundException(String user, String id, Long id1) {
    }
}
