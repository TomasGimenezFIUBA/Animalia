package com.tomasgimenez.citizen_command_service.exception.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.tomasgimenez.citizen_command_service.exception.*;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;

import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

class EntityExceptionHandlerTest {

  private EntityExceptionHandler handler;

  @BeforeEach
  void setup() {
    handler = new EntityExceptionHandler();
  }

  @Test
  void handleEntityNotFoundException_returns404WithMessage() {
    EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

    ResponseEntity<Map<String, String>> response = handler.handleEntityNotFoundException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("error", "Entity not found");
  }

  @Test
  void handleInvalidEntityReferenceException_returns400WithMessage() {
    InvalidEntityReferenceException ex = new InvalidEntityReferenceException("Invalid reference");

    ResponseEntity<Map<String, String>> response = handler.handleInvalidEntityReferenceException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).containsEntry("error", "Invalid reference");
  }

  @Test
  void handleRolePolicyException_returns409WithMessage() {
    RolePolicyException ex = new RolePolicyException("Role policy violation");

    ResponseEntity<Map<String, String>> response = handler.handleRolePolicyException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).containsEntry("error", "Role policy violation");
  }

  @Test
  void handlePessimisticLockException_returns409WithMessage() {
    PessimisticLockException ex = new PessimisticLockException();

    ResponseEntity<Map<String, String>> response = handler.handlePessimisticLockException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).containsEntry("error", "Resource is currently locked. Please try again later.");
  }
}