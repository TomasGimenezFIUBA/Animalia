package com.tomasgimenez.citizen_command_service.exception;

public class EntityPersistenceException extends RuntimeException {

  public EntityPersistenceException(String message) {
    super(message);
  }

  public EntityPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

}
