package com.tomasgimenez.citizen_query_service.exception;

public class DuplicatedCitizenException extends RuntimeException {

    public DuplicatedCitizenException(String id) {
        super("Citizen with ID " + id + " already exists.");
    }
}
