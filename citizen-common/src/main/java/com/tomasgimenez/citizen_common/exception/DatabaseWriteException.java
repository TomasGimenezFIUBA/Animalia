package com.tomasgimenez.citizen_common.exception;

public class DatabaseWriteException extends RuntimeException {

  public DatabaseWriteException(String message) {
    super(message);
  }

  public DatabaseWriteException(String message, Throwable cause) {
    super(message, cause);
  }

}
