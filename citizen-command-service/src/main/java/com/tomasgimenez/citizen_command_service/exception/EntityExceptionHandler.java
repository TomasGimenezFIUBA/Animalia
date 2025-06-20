package com.tomasgimenez.citizen_command_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PessimisticLockException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class EntityExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage() != null ? ex.getMessage() : "Invalid entity");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(RolePolicyException.class)
  public ResponseEntity<Map<String, String>> handleRolePolicyException(RolePolicyException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage() != null ? ex.getMessage() : "Role policy violation");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(EntityConflictException.class)
  public ResponseEntity<Map<String, String>> handlePessimisticLockException(PessimisticLockException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Resource is currently locked. Please try again later.");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(EntityPersistenceException.class)
  public ResponseEntity<Map<String, String>> handleEntityPersistenceException(EntityPersistenceException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage() != null ? ex.getMessage() : "Error persisting entity");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

