package com.tomasgimenez.citizen_query_service.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface CitizenListener {
  void handleCitizenEvent(ConsumerRecord<String, byte[]> citizenEventRecord, Acknowledgment ack);
}
