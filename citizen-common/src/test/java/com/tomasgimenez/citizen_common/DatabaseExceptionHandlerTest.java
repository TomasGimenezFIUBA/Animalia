package com.tomasgimenez.citizen_common;

import static org.assertj.core.api.Assertions.assertThat;

import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;
import com.tomasgimenez.citizen_common.exception.handler.DatabaseExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

class DatabaseExceptionHandlerTest {

  private DatabaseExceptionHandler handler;

  @BeforeEach
  void setup() {
    handler = new DatabaseExceptionHandler();
  }

  @Test
  void handleEntityPersistenceException_returns500WithMessage() {
    DatabaseWriteException ex = new DatabaseWriteException("Persistence error");

    ResponseEntity<Map<String, String>> response = handler.handleEntityPersistenceException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).containsEntry("error", "Persistence error");
  }

  @Test
  void handleDatabaseAccessException_returns500WithMessage() {
    DatabaseReadException ex = new DatabaseReadException("Database access error", new RuntimeException());

    ResponseEntity<Map<String, String>> response = handler.handleDatabaseAccessException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).containsEntry("error", "Database access error");
  }
}