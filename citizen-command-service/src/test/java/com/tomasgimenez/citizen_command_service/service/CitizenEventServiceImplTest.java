package com.tomasgimenez.citizen_command_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tomasgimenez.animalia.avro.CitizenEventEnvelope;
import com.tomasgimenez.citizen_command_service.config.KafkaTopics;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;
import com.tomasgimenez.citizen_command_service.mapper.CitizenEventMapper;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.repository.CitizenEventRepository;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.SerializationException;
import com.tomasgimenez.citizen_common.kafka.AvroSerializer;

class CitizenEventServiceImplTest {

  @Mock private CitizenEventRepository repository;
  @Mock
  private CitizenEventMapper mapper;
  @Mock private AvroSerializer serializer;
  @Mock private KafkaTopics kafkaTopics;

  @InjectMocks
  private CitizenEventServiceImpl service;

  private final UUID citizenId = UUID.randomUUID();
  private final CitizenEntity citizen = CitizenEntity.builder().id(citizenId).build();
  private final CitizenEventEnvelope envelope = new CitizenEventEnvelope();
  private final byte[] serializedPayload = "payload".getBytes();

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    when(kafkaTopics.getCitizenEvent()).thenReturn("citizen.events");
  }

  @Test
  void createCreatedEvent_shouldPersistEvent() throws SerializationException {
    when(mapper.toCreatedEvent(citizen)).thenReturn(envelope);
    when(serializer.serialize(envelope)).thenReturn(serializedPayload);

    service.createCreatedEvent(citizen);

    verify(repository, times(1)).save(any(CitizenEventEntity.class));
  }

  @Test
  void createUpdatedEvent_shouldPersistEvent() throws SerializationException {
    when(mapper.toUpdatedEvent(citizen)).thenReturn(envelope);
    when(serializer.serialize(envelope)).thenReturn(serializedPayload);

    service.createUpdatedEvent(citizen);

    verify(repository, times(1)).save(any(CitizenEventEntity.class));
  }

  @Test
  void createDeletedEvent_shouldPersistEvent() throws SerializationException {
    when(mapper.toDeletedEvent(citizenId)).thenReturn(envelope);
    when(serializer.serialize(envelope)).thenReturn(serializedPayload);

    service.createDeletedEvent(citizenId);

    verify(repository, times(1)).save(any(CitizenEventEntity.class));
  }

  @Test
  void persistEvent_shouldThrowEntityPersistenceException_onSerializationError() throws SerializationException {
    when(mapper.toCreatedEvent(citizen)).thenReturn(envelope);
    when(serializer.serialize(envelope)).thenThrow(new SerializationException("fail", new RuntimeException()));

    DatabaseWriteException ex = assertThrows(
        DatabaseWriteException.class,
        () -> service.createCreatedEvent(citizen)
    );

    assertInstanceOf(SerializationException.class, ex.getCause());
    verify(repository, never()).save(any());
  }

  @Test
  void persistEvent_shouldThrowEntityPersistenceException_onDatabaseError() throws SerializationException {
    when(mapper.toCreatedEvent(citizen)).thenReturn(envelope);
    when(serializer.serialize(envelope)).thenReturn(serializedPayload);
    when(repository.save(any())).thenThrow(new RuntimeException("db error"));

    DatabaseWriteException ex = assertThrows(
        DatabaseWriteException.class,
        () -> service.createCreatedEvent(citizen)
    );

    assertEquals("Failed to persist event", ex.getMessage());
  }

  @Test
  void getOldestUnprocessedPerAggregateId_shouldReturnEvents() {
    List<CitizenEventEntity> events = List.of(mock(CitizenEventEntity.class));
    when(repository.findOldestUnprocessedPerAggregateId(5)).thenReturn(events);

    List<CitizenEventEntity> result = service.getOldestUnprocessedPerAggregateId(5);

    verify(repository, times(1)).findOldestUnprocessedPerAggregateId(5);
    assertEquals(events, result);
  }

  @Test
  void getOldestUnprocessedPerAggregateId_shouldThrowDatabaseAccessException_onFailure() {
    when(repository.findOldestUnprocessedPerAggregateId(5))
        .thenThrow(new RuntimeException("db down"));

    DatabaseReadException ex = assertThrows(
        DatabaseReadException.class,
        () -> service.getOldestUnprocessedPerAggregateId(5)
    );

    assertTrue(ex.getCause().getMessage().contains("db down"));
  }

  @Test
  void markAsProcessedById_shouldMarkEventsAsProcessed() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

    service.markAllAsProcessedById(ids);

    verify(repository, times(1)).markAllAsProcessedById(ids);
  }

  @Test
  void markAsProcessedById_shouldThrowEntityPersistenceException_onFailure() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
    doThrow(new RuntimeException("db error")).when(repository).markAllAsProcessedById(ids);

    DatabaseWriteException ex = assertThrows(
        DatabaseWriteException.class,
        () -> service.markAllAsProcessedById(ids)
    );

    assertEquals("Could not mark some events as processed", ex.getMessage());
    verify(repository, times(1)).markAllAsProcessedById(ids);
  }
}
