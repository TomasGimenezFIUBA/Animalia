package com.tomasgimenez.citizen_command_service.service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CitizenEventProducerServiceImpl implements CitizenEventProducerService {
  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  @Override
  public CompletableFuture<SendResult<String, byte[]>> sendCitizenEvent(String key, byte[] payload, String topic, Consumer<SendResult<String, byte[]>> onSuccess) {
    var completableFuture =  kafkaTemplate.send(topic, key, payload)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to send CitizenEvent to topic {}: {}", topic, exception.getMessage());
                } else {
                    log.info("CitizenEvent sent successfully to topic {}: {}", topic, key);
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                }
            });
    log.info("CitizenEvent sent to topic {}: {}", topic, key);

    return completableFuture;
  }
}