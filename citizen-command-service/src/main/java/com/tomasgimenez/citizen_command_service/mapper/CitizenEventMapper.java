package com.tomasgimenez.citizen_command_service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.animalia.avro.Species;
import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.dto.SpeciesDTO;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;

@Component
public class CitizenEventMapper {
  @Value("${spring.application.name}")
  private String source;
  public CitizenCreatedEvent toCreatedEvent(CitizenEntity citizen) {
    var dto = CitizenDTO.fromEntity(citizen);
    return CitizenCreatedEvent.newBuilder()
        .setEventId(UUID.randomUUID().toString())
        .setTimestamp(Instant.now().toString())
        .setSource(source)
        .setId(dto.id().toString())
        .setName(dto.name())
        .setHasHumanPet(dto.hasHumanPet())
        .setSpecies(toSpeciesAvro(dto.speciesDTO()))
        .setRoleNames(new ArrayList<>(dto.roleNames().stream().map(Enum::name).toList()))
        .build();
  }

  private Species toSpeciesAvro(SpeciesDTO dto) {
    return Species.newBuilder()
        .setId(dto.id().toString())
        .setName(dto.name())
        .setWeight(dto.weight())
        .setHeight(dto.height())
        .build();
  }

  public CitizenUpdatedEvent toUpdatedEvent(CitizenEntity entity) {
    var dto = CitizenDTO.fromEntity(entity);
    return CitizenUpdatedEvent.newBuilder()
        .setEventId(UUID.randomUUID().toString())
        .setTimestamp(Instant.now().toString())
        .setSource(source)
        .setId(dto.id().toString())
        .setName(dto.name())
        .setHasHumanPet(dto.hasHumanPet())
        .setSpecies(toSpeciesAvro(dto.speciesDTO()))
        .setRoleNames(new ArrayList<>(dto.roleNames().stream().map(Enum::name).toList()))
        .build();
  }

  public CitizenDeletedEvent toDeletedEvent(UUID id) {
    return CitizenDeletedEvent.newBuilder()
        .setEventId(UUID.randomUUID().toString())
        .setTimestamp(Instant.now().toString())
        .setSource(source)
        .setId(id.toString())
        .build();
  }
}