package com.tomasgimenez.citizen_query_service.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface CitizenListener {
  void handleCitizenCreatedEvent(ConsumerRecord<String, byte[]> createdCitizenEventRecord, Acknowledgment ack);
  void handleCitizenUpdatedEvent(ConsumerRecord<String, byte[]> updatedCitizenEventRecord, Acknowledgment ack);
  void handleCitizenDeletedEvent(ConsumerRecord<String, byte[]> deletedCitizenEventRecord, Acknowledgment ack);
}
