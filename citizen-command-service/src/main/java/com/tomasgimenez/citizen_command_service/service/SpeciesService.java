package com.tomasgimenez.citizen_command_service.service;

import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;

public interface SpeciesService {
  SpeciesEntity getById(UUID id);
  Set<SpeciesEntity> getByIds(Set<UUID> ids);
}
