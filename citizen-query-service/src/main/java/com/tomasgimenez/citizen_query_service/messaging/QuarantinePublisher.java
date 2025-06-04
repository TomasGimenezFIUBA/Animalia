package com.tomasgimenez.citizen_query_service.messaging;

public interface QuarantinePublisher {
  void publishToQuarantine(String citizenId, byte[] payload, String originalTopic, String reason);
}
