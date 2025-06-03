package com.tomasgimenez.citizen_command_service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenEventEnvelope;
import com.tomasgimenez.animalia.avro.CitizenEventType;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.animalia.avro.Species;
import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.dto.SpeciesDTO;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;

import lombok.Setter;

@Component
@Setter
public class CitizenEventMapper {
  @Value("${spring.application.name}")
  private String source;

  public CitizenEventEnvelope toCreatedEvent(CitizenEntity citizen) {
    var dto = CitizenDTO.fromEntity(citizen);
    var event = CitizenCreatedEvent.newBuilder()
        .setEventId(UUID.randomUUID().toString())
        .setTimestamp(Instant.now().toString())
        .setSource(source)
        .setId(dto.id().toString())
        .setName(dto.name())
        .setHasHumanPet(dto.hasHumanPet())
        .setSpecies(toSpeciesAvro(dto.speciesDTO()))
        .setRoleNames(new ArrayList<>(dto.roleNames().stream().map(Enum::name).toList()))
        .build();
    return toEnvelopeEvent(event, CitizenEventType.CREATED);
  }

  public CitizenEventEnvelope toUpdatedEvent(CitizenEntity entity) {
    var dto = CitizenDTO.fromEntity(entity);
    var event = CitizenUpdatedEvent.newBuilder()
        .setEventId(UUID.randomUUID().toString())
        .setTimestamp(Instant.now().toString())
        .setSource(source)
        .setId(dto.id().toString())
        .setName(dto.name())
        .setHasHumanPet(dto.hasHumanPet())
        .setSpecies(toSpeciesAvro(dto.speciesDTO()))
        .setRoleNames(new ArrayList<>(dto.roleNames().stream().map(Enum::name).toList()))
        .build();
    return toEnvelopeEvent(event, CitizenEventType.UPDATED);
  }

  public CitizenEventEnvelope toDeletedEvent(UUID id) {
    var event = CitizenDeletedEvent.newBuilder()
        .setEventId(UUID.randomUUID().toString())
        .setTimestamp(Instant.now().toString())
        .setSource(source)
        .setId(id.toString())
        .build();

    return toEnvelopeEvent(event, CitizenEventType.DELETED);
  }

  private <T extends SpecificRecordBase> CitizenEventEnvelope toEnvelopeEvent(T event, CitizenEventType type) {
    return CitizenEventEnvelope.newBuilder()
        .setEventType(type)
        .setEventId(UUID.randomUUID().toString())
        .setPayload(event)
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
}