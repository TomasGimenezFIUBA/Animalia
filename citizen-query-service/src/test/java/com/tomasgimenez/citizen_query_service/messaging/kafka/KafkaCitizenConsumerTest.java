package com.tomasgimenez.citizen_query_service.messaging.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import com.tomasgimenez.animalia.avro.*;
import com.tomasgimenez.citizen_common.exception.DeserializationException;
import com.tomasgimenez.citizen_common.exception.MessageProductionException;
import com.tomasgimenez.citizen_common.kafka.AvroDeserializer;
import com.tomasgimenez.citizen_query_service.messaging.CitizenEventHandler;
import com.tomasgimenez.citizen_query_service.messaging.QuarantinePublisher;
import com.tomasgimenez.citizen_query_service.service.EventDeduplicationService;
import com.tomasgimenez.citizen_query_service.service.QuarantineService;

public class KafkaCitizenConsumerTest {

  private AvroDeserializer deserializer;
  private CitizenEventHandler eventHandler;
  private QuarantineService quarantineService;
  private QuarantinePublisher quarantinePublisher;
  private EventDeduplicationService deduplicationService;
  private KafkaCitizenConsumer listener;
  private Acknowledgment ack;

  @BeforeEach
  void setUp() {
    deserializer = mock(AvroDeserializer.class);
    eventHandler = mock(CitizenEventHandler.class);
    quarantineService = mock(QuarantineService.class);
    quarantinePublisher = mock(QuarantinePublisher.class);
    deduplicationService = mock(EventDeduplicationService.class);
    listener = new KafkaCitizenConsumer(deserializer, eventHandler, quarantineService, quarantinePublisher, deduplicationService);
    ack = mock(Acknowledgment.class);
  }

  @Test
  void shouldSkipIfCitizenIsInQuarantine() throws MessageProductionException {
    var record = record("1", new byte[]{});

    when(quarantineService.isInQuarantine("1")).thenReturn(true);

    listener.handleCitizenEvent(record, ack);

    verify(quarantinePublisher).publishToQuarantine(eq("1"), any(), any(), eq("Citizen is in quarantine"));
    verify(ack).acknowledge();
    verifyNoInteractions(deserializer, eventHandler);
  }

  @Test
  void shouldSkipIfEventAlreadyProcessed() throws DeserializationException, MessageProductionException {
    var eventId = UUID.randomUUID();
    var payload = new CitizenCreatedEvent(
        eventId.toString(),
        "2024-01-01T10:00:00Z",
        "test-source",
        "cid-1",
        "Jane",
        true,
        new Species("s1", "Human", 70.0, 1.8),
        List.of("admin")
    );

    var envelope = CitizenEventEnvelope.newBuilder()
        .setEventId(eventId.toString())
        .setEventType(CitizenEventType.CREATED)
        .setPayload(payload)
        .build();

    var record = record("1", new byte[]{});

    when(quarantineService.isInQuarantine("1")).thenReturn(false);
    when(deserializer.deserialize(any(), eq(CitizenEventEnvelope.class))).thenReturn(envelope);
    when(deduplicationService.isEventProcessed(eventId.toString())).thenReturn(true);

    listener.handleCitizenEvent(record, ack);

    verify(ack).acknowledge();
    verifyNoMoreInteractions(eventHandler);
  }

  @Test
  void shouldHandleCreatedEvent() throws DeserializationException, MessageProductionException {
    var id = UUID.randomUUID();
    var eventId = UUID.randomUUID().toString();
    var event = new CitizenCreatedEvent(
        eventId,
        "2024-01-01T10:00:00Z",
        "source-test",
        id.toString(),
        "John",
        true,
        new Species("233", "Human", 1.0, 2.0),
        List.of("Leader")
    );

    var envelope = CitizenEventEnvelope.newBuilder()
        .setEventId(eventId)
        .setEventType(CitizenEventType.CREATED)
        .setPayload(event)
        .build();

    var record = record(id.toString(), new byte[]{});

    when(quarantineService.isInQuarantine(id.toString())).thenReturn(false);
    when(deserializer.deserialize(any(), eq(CitizenEventEnvelope.class))).thenReturn(envelope);
    when(deduplicationService.isEventProcessed(anyString())).thenReturn(false);

    listener.handleCitizenEvent(record, ack);

    verify(eventHandler).handleCreated(event);
    verify(quarantineService).resetQuarantineCounter(id.toString());
    verify(deduplicationService).markEventAsProcessed(anyString());
    verify(ack).acknowledge();
  }

  @Test
  void shouldPublishToQuarantineOnDeserializationError() throws DeserializationException, MessageProductionException {
    var record = record("1", new byte[]{});

    when(quarantineService.isInQuarantine("1"))
        .thenReturn(false)
        .thenReturn(true);
    when(deserializer.deserialize(any(), eq(CitizenEventEnvelope.class))).thenThrow(new RuntimeException("boom"));

    assertThrows(RuntimeException.class, () ->
      listener.handleCitizenEvent(record, ack)
    );

    verify(quarantineService).recordFailureForCitizen("1");
    verify(quarantinePublisher).publishToQuarantine(eq("1"), any(), any(), any());
  }

  private ConsumerRecord<String, byte[]> record(String key, byte[] value) {
    return new ConsumerRecord<>("topic", 0, 0, key, value);
  }
}
