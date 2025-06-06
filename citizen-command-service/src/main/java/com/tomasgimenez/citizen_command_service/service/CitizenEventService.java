package com.tomasgimenez.citizen_command_service.service;

import java.util.List;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;

public interface CitizenEventService {

  void createCreatedEvent(CitizenEntity citizen);

  void createUpdatedEvent(CitizenEntity citizen);

  void createDeletedEvent(UUID id);

  List<CitizenEventEntity> getOldestUnprocessedPerAggregateId(int limit);

  void markAllAsProcessedById(List<UUID> ids);
}
