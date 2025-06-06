package com.tomasgimenez.citizen_query_service.messaging.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenEventEnvelope;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.citizen_common.kafka.AvroDeserializer;
import com.tomasgimenez.citizen_query_service.messaging.CitizenEventHandler;
import com.tomasgimenez.citizen_query_service.messaging.CitizenConsumer;
import com.tomasgimenez.citizen_query_service.messaging.QuarantinePublisher;
import com.tomasgimenez.citizen_query_service.service.EventDeduplicationService;
import com.tomasgimenez.citizen_query_service.service.QuarantineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCitizenConsumer implements CitizenConsumer {

  private final AvroDeserializer avroDeserializer;
  private final CitizenEventHandler citizenEventHandler;
  private final QuarantineService quarantineService;
  private final QuarantinePublisher quarantinePublisher;
  private final EventDeduplicationService eventDeduplicationService;

  @KafkaListener(topics = "${kafka.topics.citizen-event}", groupId = "${kafka.consumer.group-id}",
      containerFactory = "kafkaListenerContainerFactory")
  @Override
  public void handleCitizenEvent(ConsumerRecord<String, byte[]> citizenEventRecord, Acknowledgment ack) {

    String citizenId = citizenEventRecord.key();

    if (handleQuarantine(citizenId, citizenEventRecord)) {
      ack.acknowledge();
      return;
    }

    try {
      CitizenEventEnvelope event = avroDeserializer.deserialize(citizenEventRecord.value(), CitizenEventEnvelope.class);

      if (eventDeduplicationService.isEventProcessed(event.getEventId().toString())) {
        log.warn("Skipping already processed event: {}", event.getEventId());
        ack.acknowledge();
        return;
      }

      Object payload = event.getPayload();

      switch (payload) {
        case CitizenCreatedEvent createdEvent -> citizenEventHandler.handleCreated(createdEvent);
        case CitizenUpdatedEvent updatedEvent -> citizenEventHandler.handleUpdated(updatedEvent);
        case CitizenDeletedEvent deletedEvent -> citizenEventHandler.handleDeleted(deletedEvent);
        default -> throw new IllegalArgumentException("Unknown payload type: " + payload.getClass());
      }

      ack.acknowledge();
      quarantineService.resetQuarantineCounter(citizenId);
      eventDeduplicationService.markEventAsProcessed(event.getEventId().toString());
    } catch (Exception e) {
      quarantineService.recordFailureForCitizen(citizenId);
      handleQuarantine(citizenId, citizenEventRecord);

      log.error("Failed to process event for citizen {}: {}", citizenId, e.getMessage(), e);
      throw new RuntimeException("Error processing citizen event", e);
    }
  }

  private boolean handleQuarantine(String citizenId, ConsumerRecord<String, byte[]> consumerRecord) {
    if (quarantineService.isInQuarantine(citizenId)) {
      log.warn("Skipping event for quarantined citizen: {}", citizenId);
      quarantinePublisher.publishToQuarantine(citizenId, consumerRecord.value(), consumerRecord.topic(),
          "Citizen is in quarantine");
      return true;
    }
    return false;
  }
}
