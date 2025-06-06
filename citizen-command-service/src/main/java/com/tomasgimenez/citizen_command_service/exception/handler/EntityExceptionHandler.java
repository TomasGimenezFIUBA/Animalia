package com.tomasgimenez.citizen_command_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import jakarta.persistence.PessimisticLockException;

import java.util.HashMap;
import java.util.Map;

import com.tomasgimenez.citizen_command_service.exception.EntityConflictException;
import com.tomasgimenez.citizen_command_service.exception.EntityPersistenceException;
import com.tomasgimenez.citizen_command_service.exception.InvalidEntityReferenceException;
import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_common.exception.DatabaseAccessException;

@ControllerAdvice
public class EntityExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
    return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage() != null ? ex.getMessage() : "Entity Not Found");
  }

  @ExceptionHandler(InvalidEntityReferenceException.class)
  public ResponseEntity<Map<String, String>> handleInvalidEntityReferenceException(InvalidEntityReferenceException ex) {
    return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage() != null ? ex.getMessage() : "Invalid entity reference");
  }

  @ExceptionHandler(RolePolicyException.class)
  public ResponseEntity<Map<String, String>> handleRolePolicyException(RolePolicyException ex) {
    return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage() != null ? ex.getMessage() : "Role policy violation");
  }

  @ExceptionHandler(EntityConflictException.class)
  public ResponseEntity<Map<String, String>> handlePessimisticLockException(PessimisticLockException ex) {
    return createErrorResponse(HttpStatus.CONFLICT, "Resource is currently locked. Please try again later.");
  }

  @ExceptionHandler(EntityPersistenceException.class)
  public ResponseEntity<Map<String, String>> handleEntityPersistenceException(EntityPersistenceException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Error persisting entity");
  }

  @ExceptionHandler(DatabaseAccessException.class)
  public ResponseEntity<Map<String, String>> handleDatabaseAccessException(DatabaseAccessException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Error persisting entity");
  }

  private ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
    Map<String, String> error = new HashMap<>();
    error.put("error", message);
    return ResponseEntity.status(status).body(error);
  }
}