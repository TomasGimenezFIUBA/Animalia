package com.tomasgimenez.citizen_command_service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ValidationExceptionHandlerTest {

  private ValidationExceptionHandler handler;

  @BeforeEach
  void setup() {
    handler = new ValidationExceptionHandler();
  }

  @Test
  void handleValidationExceptions_returns400WithFieldErrors() {
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
    bindingResult.addError(new FieldError("testObject", "field1", "must not be blank"));
    bindingResult.addError(new FieldError("testObject", "field2", "size must be between 1 and 10"));

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Map<String, String> errors = response.getBody();
    assertThat(errors).containsEntry("field1", "must not be blank");
    assertThat(errors).containsEntry("field2", "size must be between 1 and 10");
  }
}
