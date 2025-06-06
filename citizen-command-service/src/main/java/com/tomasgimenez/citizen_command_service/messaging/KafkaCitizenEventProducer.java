package com.tomasgimenez.citizen_command_service.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_common.exception.MessageProductionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaCitizenEventProducer implements CitizenEventProducer {
  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  @Override
  public CompletableFuture<SendResult<String, byte[]>> sendCitizenEvent(String key, byte[] payload, String topic, Consumer<SendResult<String, byte[]>> onSuccess)
      throws MessageProductionException {
    try {
      return kafkaTemplate.send(topic, key, payload)
          .whenComplete((result, exception) -> {
            if (exception != null) {
              log.error("Failed to send CitizenEvent to topic {}: {}", topic, exception.getMessage());
            } else {
              log.debug("CitizenEvent sent successfully to topic {}: {}", topic, key);
              if (onSuccess != null) {
                onSuccess.accept(result);
              }
            }
          });
    } catch (Exception e){
      log.error("Error sending CitizenEvent to topic {}: {}", topic, e.getMessage(), e);
      throw new MessageProductionException("Failed to send CitizenEvent", e);
    }
  }
}