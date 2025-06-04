package com.tomasgimenez.citizen_query_service.exception;

public class CorruptedCitizenDocumentException extends RuntimeException {
  public CorruptedCitizenDocumentException(String message) {
    super(message);
  }
}

