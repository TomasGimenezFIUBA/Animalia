package com.tomasgimenez.citizen_query_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CitizenExceptionHandler {

  @ExceptionHandler(CitizenNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(CitizenNotFoundException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage() != null ? ex.getMessage() : "Citizen not found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleRolePolicyException(Exception ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Internal server error. Please try again later.");
    log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}

