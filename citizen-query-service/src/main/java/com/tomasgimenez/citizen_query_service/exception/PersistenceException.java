package com.tomasgimenez.citizen_query_service.exception;

public class PersistenceException extends RuntimeException {

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}