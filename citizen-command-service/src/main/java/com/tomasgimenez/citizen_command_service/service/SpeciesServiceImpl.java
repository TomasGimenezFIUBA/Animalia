package com.tomasgimenez.citizen_command_service.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_command_service.config.CacheConfig;
import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;
import com.tomasgimenez.citizen_command_service.repository.SpeciesRepository;
import com.tomasgimenez.citizen_common.exception.DatabaseAccessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeciesServiceImpl implements SpeciesService {

  private final SpeciesRepository speciesRepository;

  @Cacheable(value = CacheConfig.SPECIES_CACHE, key = "#id")
  @Override
  public SpeciesEntity getById(UUID id) {
    Optional<SpeciesEntity> optionalSpecies;

    try {
       optionalSpecies = speciesRepository.findById(id);
    } catch (Exception e) {
      log.error("Error fetching species by ID: {}", id, e);
      throw new DatabaseAccessException(
          "Error accessing database for species with ID: " + id, e);
    }

    return optionalSpecies.orElseThrow(() -> {
      log.warn("Species not found for ID: {}", id);
      return new EntityNotFoundException("Species not found for ID: " + id);
    });
  }

  @Override
  public Set<SpeciesEntity> getByIds(Set<UUID> ids) {
      return ids.stream()
          .map(this::getById) // could be optimized but this is clear and uses cache
          .collect(Collectors.toSet());
  }
}
