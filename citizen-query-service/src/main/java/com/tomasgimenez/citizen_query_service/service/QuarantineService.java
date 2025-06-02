package com.tomasgimenez.citizen_query_service.service;

public interface QuarantineService {
  boolean isInQuarantine(String citizenId);

  boolean recordFailureForCitizen(String citizenId);

  void resetQuarantineCounter(String citizenId);
}
