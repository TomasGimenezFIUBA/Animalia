package com.tomasgimenez.citizen_query_service.messaging.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_common.exception.MessageProductionException;
import com.tomasgimenez.citizen_query_service.config.KafkaTopics;
import com.tomasgimenez.citizen_query_service.messaging.QuarantinePublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaQuarantinePublisherImpl implements QuarantinePublisher {

  private final KafkaTemplate<String, byte[]> kafkaTemplate;
  private final KafkaTopics kafkaTopics;

  @Override
  public void publishToQuarantine(String citizenId, byte[] payload, String originalTopic, String reason)
      throws MessageProductionException {
    try {
      String quarantineTopic = kafkaTopics.getCitizenQuarantine();
      kafkaTemplate.send(quarantineTopic, citizenId, payload);
      log.warn("Published quarantined event for citizen {} to topic {}. Reason: {}",
          citizenId, quarantineTopic, reason);
    } catch (Exception e) {
      log.error("Failed to publish quarantined event for citizen {}: {}", citizenId, e.getMessage(), e);
      throw new MessageProductionException(
          "Failed to publish quarantined event for citizen " + citizenId, e);
    }
  }
}