package com.tomasgimenez.citizen_query_service.messaging.kafka;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.tomasgimenez.citizen_query_service.config.KafkaTopics;

class KafkaQuarantinePublisherImplTest {

  private KafkaTemplate<String, byte[]> kafkaTemplate;
  private KafkaTopics kafkaTopics;
  private KafkaQuarantinePublisherImpl publisher;

  @BeforeEach
  void setUp() {
    kafkaTemplate = mock(KafkaTemplate.class);
    kafkaTopics = mock(KafkaTopics.class);
    publisher = new KafkaQuarantinePublisherImpl(kafkaTemplate, kafkaTopics);
  }

  @Test
  void publishToQuarantine_successfulSend_logsWarning() {
    String citizenId = "123";
    byte[] payload = new byte[]{1, 2, 3};
    String quarantineTopic = "quarantine-topic";
    String reason = "invalid format";

    when(kafkaTopics.getCitizenQuarantine()).thenReturn(quarantineTopic);

    publisher.publishToQuarantine(citizenId, payload, "original-topic", reason);

    verify(kafkaTemplate).send(quarantineTopic, citizenId, payload);
  }

  @Test
  void publishToQuarantine_sendThrowsException_logsError() {
    String citizenId = "456";
    byte[] payload = new byte[]{4, 5, 6};
    String quarantineTopic = "quarantine-topic";
    String reason = "deserialization error";

    when(kafkaTopics.getCitizenQuarantine()).thenReturn(quarantineTopic);
    doThrow(new RuntimeException("Kafka down")).when(kafkaTemplate).send(anyString(), anyString(), any());

    publisher.publishToQuarantine(citizenId, payload, "original-topic", reason);

    verify(kafkaTemplate).send(quarantineTopic, citizenId, payload);
  }
}
