package com.tomasgimenez.citizen_query_service.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import com.tomasgimenez.citizen_common.exception.MessageProductionException;

public interface CitizenConsumer {
  void handleCitizenEvent(ConsumerRecord<String, byte[]> citizenEventRecord, Acknowledgment ack)
      throws MessageProductionException;
}
