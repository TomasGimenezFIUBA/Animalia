package com.tomasgimenez.citizen_command_service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EntityExceptionHandlerTest {

  private EntityExceptionHandler handler;

  @BeforeEach
  void setup() {
    handler = new EntityExceptionHandler();
  }

  @Test
  void handleEntityNotFoundException_returns404WithMessage() {
    EntityNotFoundException ex = new EntityNotFoundException("Entity not found with id 123");

    ResponseEntity<Map<String, String>> response = handler.handleEntityNotFoundException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("error", "Entity not found with id 123");
  }

  @Test
  void handleEntityNotFoundException_nullMessage_returnsDefaultMessage() {
    EntityNotFoundException ex = new EntityNotFoundException();

    ResponseEntity<Map<String, String>> response = handler.handleEntityNotFoundException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("error", "Entity not found");
  }

  @Test
  void handleRolePolicyException_returns403WithMessage() {
    RolePolicyException ex = new RolePolicyException("Role policy violation for role GENERAL");

    ResponseEntity<Map<String, String>> response = handler.handleRolePolicyException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).containsEntry("error", "Role policy violation for role GENERAL");
  }

  @Test
  void handleRolePolicyException_nullMessage_returnsDefaultMessage() {
    RolePolicyException ex = new RolePolicyException(null);

    ResponseEntity<Map<String, String>> response = handler.handleRolePolicyException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).containsEntry("error", "Role policy violation");
  }
}
