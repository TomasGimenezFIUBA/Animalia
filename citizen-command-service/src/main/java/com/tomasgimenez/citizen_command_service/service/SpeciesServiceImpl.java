package com.tomasgimenez.citizen_command_service.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_command_service.config.CacheConfig;
import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;
import com.tomasgimenez.citizen_command_service.repository.SpeciesRepository;

import jakarta.persistence.EntityNotFoundException;
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
    return speciesRepository.findById(id).orElseThrow(() -> {
      log.warn("Species not found for ID: {}", id);
      return new EntityNotFoundException("Species not found with ID: " + id);
    });
  }

  @Override
  public Set<SpeciesEntity> getByIds(Set<UUID> ids) {
    Set<SpeciesEntity> speciesSet = ids.stream()
        .map(this::getById) // could be optimized but this is clear and use cache
        .collect(Collectors.toSet());

    if (speciesSet.size() != ids.size()) {
      log.warn("Not all species found. Requested IDs: {}, Found count: {}", ids, speciesSet.size());
      throw new EntityNotFoundException("Not all species found for IDs: " + ids);
    }

    return speciesSet;
  }
}
