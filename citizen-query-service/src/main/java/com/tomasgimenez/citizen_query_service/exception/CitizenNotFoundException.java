package com.tomasgimenez.citizen_query_service.exception;

public class CitizenNotFoundException extends RuntimeException {

    public CitizenNotFoundException(String id) {
        super("Citizen with ID " + id + " not found");
    }
}
