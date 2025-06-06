package com.tomasgimenez.citizen_command_service.exception;

public class InvalidEntityReferenceException extends RuntimeException {
    public InvalidEntityReferenceException(String message) {
        super(message);
    }
}