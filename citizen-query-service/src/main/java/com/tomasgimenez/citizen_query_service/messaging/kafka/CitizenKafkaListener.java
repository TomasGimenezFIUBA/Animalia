package com.tomasgimenez.citizen_query_service.messaging.kafka;

import java.util.function.Consumer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.citizen_common.kafka.AvroDeserializer;
import com.tomasgimenez.citizen_query_service.messaging.CitizenEventHandler;
import com.tomasgimenez.citizen_query_service.messaging.CitizenListener;
import com.tomasgimenez.citizen_query_service.messaging.QuarantinePublisher;
import com.tomasgimenez.citizen_query_service.service.QuarantineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitizenKafkaListener implements CitizenListener {
  private final AvroDeserializer avroDeserializer;
  private final CitizenEventHandler citizenEventHandler;
  private final QuarantineService quarantineService;
  private final QuarantinePublisher quarantinePublisher;

  @KafkaListener(topics = "${kafka.topics.citizen-created}", groupId = "${kafka.consumer.group-id}",
                 containerFactory = "kafkaListenerContainerFactory")
  @Override
  public void handleCitizenCreatedEvent(ConsumerRecord<String, byte[]> createdCitizenEventRecord, Acknowledgment ack) {
    handleEvent(createdCitizenEventRecord, ack, CitizenCreatedEvent.class, citizenEventHandler::handleCreated);
  }

  @KafkaListener(topics = "${kafka.topics.citizen-updated}", groupId = "${kafka.consumer.group-id}",
                 containerFactory = "kafkaListenerContainerFactory")
  @Override
  public void handleCitizenUpdatedEvent(ConsumerRecord<String, byte[]> updatedCitizenEventRecord, Acknowledgment ack) {
    handleEvent(updatedCitizenEventRecord, ack, CitizenUpdatedEvent.class, citizenEventHandler::handleUpdated);
  }

  @KafkaListener(topics = "${kafka.topics.citizen-deleted}", groupId = "${kafka.consumer.group-id}",
                 containerFactory = "kafkaListenerContainerFactory")
  @Override
  public void handleCitizenDeletedEvent(ConsumerRecord<String, byte[]> deletedCitizenEventRecord, Acknowledgment ack) {
    handleEvent(deletedCitizenEventRecord, ack, CitizenDeletedEvent.class, citizenEventHandler::handleDeleted);
  }

  private <T extends SpecificRecordBase> void handleEvent(ConsumerRecord<String, byte[]> consumerRecord, Acknowledgment ack,
                           Class<T> eventClass,
                           Consumer<T> eventHandler) {
    String citizenId = consumerRecord.key();

    if (handleQuarantine(citizenId, consumerRecord)) {
      ack.acknowledge();
      return;
    }

    try {
      T event = avroDeserializer.deserialize(consumerRecord.value(), eventClass);
      eventHandler.accept(event);
      ack.acknowledge();
      quarantineService.resetQuarantineCounter(citizenId);
    } catch (Exception e) {
      quarantineService.recordFailureForCitizen(citizenId);
      handleQuarantine(citizenId, consumerRecord);

      log.error("Failed to process event for citizen {}: {}", citizenId, e.getMessage());
      throw e;
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
