package com.tomasgimenez.citizen_common.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;

@ControllerAdvice
public class DatabaseExceptionHandler {

  @ExceptionHandler(DatabaseWriteException.class)
  public ResponseEntity<Map<String, String>> handleEntityPersistenceException(DatabaseWriteException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Error persisting entity");
  }

  @ExceptionHandler(DatabaseReadException.class)
  public ResponseEntity<Map<String, String>> handleDatabaseAccessException(DatabaseReadException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Error persisting entity");
  }

  private ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
    Map<String, String> error = new HashMap<>();
    error.put("error", message);
    return ResponseEntity.status(status).body(error);
  }
}
