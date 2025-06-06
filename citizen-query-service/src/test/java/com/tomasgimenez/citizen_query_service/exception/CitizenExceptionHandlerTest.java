package com.tomasgimenez.citizen_query_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.tomasgimenez.citizen_query_service.exception.handler.CitizenExceptionHandler;

class CitizenExceptionHandlerTest {

  private final CitizenExceptionHandler handler = new CitizenExceptionHandler();

  @Test
  void handleCitizenNotFoundException_returns404WithMessage() {
    CitizenNotFoundException ex = new CitizenNotFoundException("Citizen abc123 not found");

    ResponseEntity<Map<String, String>> response = handler.handleEntityNotFoundException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("error", "Citizen with ID Citizen abc123 not found not found");
  }

  @Test
  void handleCitizenNotFoundException_returnsDefaultMessageWhenNull() {
    CitizenNotFoundException ex = new CitizenNotFoundException(null);

    ResponseEntity<Map<String, String>> response = handler.handleEntityNotFoundException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("error", "Citizen with ID null not found");
  }
}
