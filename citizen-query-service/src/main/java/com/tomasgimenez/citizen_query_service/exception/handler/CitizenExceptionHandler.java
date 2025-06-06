package com.tomasgimenez.citizen_query_service.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tomasgimenez.citizen_query_service.exception.CitizenNotFoundException;

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
}

