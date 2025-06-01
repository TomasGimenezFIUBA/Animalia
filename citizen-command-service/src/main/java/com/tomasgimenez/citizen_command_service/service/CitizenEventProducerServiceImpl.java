package com.tomasgimenez.citizen_command_service.service;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.citizen_command_service.config.KafkaProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
class CitizenEventProducerServiceImpl implements CitizenEventProducerService {
  private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
  private final KafkaProperties kafkaProperties;

  @Override
  public void sendCitizenCreatedEvent(CitizenCreatedEvent event) {
    kafkaTemplate.send(kafkaProperties.getTopic().getCitizenCreated(), event.getId().toString(), event);
    log.info("CitizenCreatedEvent sent: {}", event);
  }

  @Override
  public void sendCitizenUpdatedEvent(CitizenUpdatedEvent event) {
    kafkaTemplate.send(kafkaProperties.getTopic().getCitizenUpdated(), event.getId().toString(), event)
            .handle(
                (result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send CitizenUpdatedEvent: {}", ex.getMessage());
                    } else {
                        log.info("CitizenUpdatedEvent sent successfully: {}", event);
                    }
                    return result;
                }
            );
    log.info("CitizenUpdatedEvent sent: {}", event);
  }

  @Override
  public void sendCitizenDeletedEvent(CitizenDeletedEvent event) {
    kafkaTemplate.send(kafkaProperties.getTopic().getCitizenDeleted(), event.getId().toString(), event);
    log.info("CitizenDeletedEvent sent: {}", event);
  }
}