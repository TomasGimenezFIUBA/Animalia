package com.tomasgimenez.citizen_query_service.messaging;

import com.tomasgimenez.citizen_common.exception.MessageProductionException;

public interface QuarantinePublisher {
  void publishToQuarantine(String citizenId, byte[] payload, String originalTopic, String reason)
      throws MessageProductionException;
}
