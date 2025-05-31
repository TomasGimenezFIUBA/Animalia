package com.tomasgimenez.citizen_command_service.exception;

public class RolePolicyException extends RuntimeException {
    public RolePolicyException(String message) {
        super(message);
    }

    public RolePolicyException(String message, Throwable cause) {
        super(message, cause);
    }

}
