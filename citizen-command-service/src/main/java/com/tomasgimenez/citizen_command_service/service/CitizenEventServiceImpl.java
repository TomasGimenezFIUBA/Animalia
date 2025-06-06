package com.tomasgimenez.citizen_command_service.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.type.SerializationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.tomasgimenez.animalia.avro.CitizenEventEnvelope;
import com.tomasgimenez.animalia.avro.CitizenEventType;
import com.tomasgimenez.citizen_command_service.config.KafkaTopics;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;
import com.tomasgimenez.citizen_command_service.mapper.CitizenEventMapper;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.repository.CitizenEventRepository;
import com.tomasgimenez.citizen_common.kafka.AvroSerializer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CitizenEventServiceImpl implements CitizenEventService {

  private final CitizenEventRepository citizenEventRepository;
  private final CitizenEventMapper citizenEventMapper;
  private final AvroSerializer avroSerializer;
  private final KafkaTopics kafkaTopics;

  public CitizenEventServiceImpl(
      CitizenEventRepository citizenEventRepository,
      CitizenEventMapper citizenEventMapper,
      AvroSerializer avroSerializer,
      KafkaTopics kafkaTopics) {
    this.citizenEventRepository = citizenEventRepository;
    this.citizenEventMapper = citizenEventMapper;
    this.avroSerializer = avroSerializer;
    this.kafkaTopics = kafkaTopics;
  }

  @Override
  public void createCreatedEvent(CitizenEntity citizen) {
      CitizenEventEnvelope event = citizenEventMapper.toCreatedEvent(citizen);
      persistEvent(citizen.getId(), event, CitizenEventType.CREATED);
  }

  @Override
  public void createUpdatedEvent(CitizenEntity citizen) {
      CitizenEventEnvelope event = citizenEventMapper.toUpdatedEvent(citizen);
      persistEvent(citizen.getId(), event, CitizenEventType.UPDATED);
  }

  @Override
  public void createDeletedEvent(UUID id) {
      CitizenEventEnvelope event = citizenEventMapper.toDeletedEvent(id);
      persistEvent(id, event, CitizenEventType.DELETED);
  }

  @Override
  public List<CitizenEventEntity> getOldestUnprocessedPerAggregateId(int limit) {
    try {
      return citizenEventRepository.findOldestUnprocessedPerAggregateId(limit);
    } catch (Exception e) {
      log.error("Failed to fetch unprocessed events: {}", e.getMessage(), e);
      throw new DatabaseReadException("Could not fetch unprocessed events", e);
    }
  }

  @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 2000))
  @Override
  public void markAllAsProcessedById(List<UUID> ids) {
    try {
      citizenEventRepository.markAllAsProcessedById(ids);
      log.debug("Marked {} events as processed", ids.size());
    } catch (Exception e) {
      log.error("Failed to mark events as processed: {}", e.getMessage(), e);
      throw new DatabaseWriteException("Could not mark some events as processed", e);
    }
  }

  private void persistEvent(UUID aggregateId, CitizenEventEnvelope event, CitizenEventType eventType) {
    try {
      var serializedEvent = avroSerializer.serialize(event);

      CitizenEventEntity citizenEvent = CitizenEventEntity.builder()
          .aggregateId(aggregateId)
          .type(eventType.name())
          .payload(serializedEvent)
          .processed(false)
          .topic(kafkaTopics.getCitizenEvent())
          .createdAt(Instant.now())
          .build();

      citizenEventRepository.save(citizenEvent);
      log.debug("{} event persisted for citizen ID {}", eventType, aggregateId);
    } catch (SerializationException serializationException) {
      log.error("Serialization error for {} event of citizen ID {}: {}", eventType, aggregateId, serializationException.getMessage(), serializationException);
      throw new DatabaseWriteException("Event could not be persisted due to serialization exception", serializationException);
    } catch (Exception e) {
      log.error("Database save failed for {} event of citizen ID {}: {}", eventType, aggregateId, e.getMessage(), e);
      throw new DatabaseWriteException("Failed to persist event", e);
    }
  }
}
