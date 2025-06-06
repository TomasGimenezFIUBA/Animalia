package com.tomasgimenez.citizen_command_service.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.tomasgimenez.citizen_command_service.messaging.KafkaCitizenEventProducer;
import com.tomasgimenez.citizen_common.exception.MessageProductionException;

class KafkaCitizenEventProducerTest {

  private KafkaTemplate<String, byte[]> kafkaTemplate;
  private KafkaCitizenEventProducer service;

  @BeforeEach
  void setUp() {
    kafkaTemplate = mock(KafkaTemplate.class);
    service = new KafkaCitizenEventProducer(kafkaTemplate);
  }

  @Test
  void sendCitizenEvent_shouldReturnCompletedFutureAndInvokeOnSuccess() throws MessageProductionException {
    String key = UUID.randomUUID().toString();
    String topic = "citizen-topic";
    byte[] payload = "test-payload".getBytes();

    @SuppressWarnings("unchecked")
    CompletableFuture<SendResult<String, byte[]>> mockedFuture = mock(CompletableFuture.class);
    SendResult<String, byte[]> sendResult = new SendResult<>(null,
        new RecordMetadata(new TopicPartition(topic, 0), 0, 0, System.currentTimeMillis(), Long.valueOf(0), 0, 0));

    when(kafkaTemplate.send(topic, key, payload)).thenReturn(mockedFuture);

    Consumer<SendResult<String, byte[]>> onSuccess = mock(Consumer.class);

    ArgumentCaptor<java.util.function.BiConsumer<SendResult<String, byte[]>, Throwable>> biConsumerCaptor =
        ArgumentCaptor.forClass(java.util.function.BiConsumer.class);

    service.sendCitizenEvent(key, payload, topic, onSuccess);

    verify(mockedFuture).whenComplete(biConsumerCaptor.capture());

    biConsumerCaptor.getValue().accept(sendResult, null);

    verify(onSuccess).accept(sendResult);
  }

  @Test
  void sendCitizenEvent_shouldLogErrorIfExceptionOccurs() throws MessageProductionException {
    String key = UUID.randomUUID().toString();
    String topic = "citizen-topic";
    byte[] payload = "test-payload".getBytes();

    @SuppressWarnings("unchecked")
    CompletableFuture<SendResult<String, byte[]>> mockedFuture = mock(CompletableFuture.class);

    when(kafkaTemplate.send(topic, key, payload)).thenReturn(mockedFuture);

    service.sendCitizenEvent(key, payload, topic, null);

    ArgumentCaptor<java.util.function.BiConsumer<SendResult<String, byte[]>, Throwable>> biConsumerCaptor =
        ArgumentCaptor.forClass(java.util.function.BiConsumer.class);

    verify(mockedFuture).whenComplete(biConsumerCaptor.capture());

    biConsumerCaptor.getValue().accept(null, new RuntimeException("Kafka send failed"));
  }
}

