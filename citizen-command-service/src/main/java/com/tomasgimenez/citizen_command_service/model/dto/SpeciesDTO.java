package com.tomasgimenez.citizen_command_service.model.dto;

import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;

public record SpeciesDTO (
    UUID id,
    String name,
    Double weight,
    Double height
){
  static SpeciesDTO fromEntity(SpeciesEntity species) {
    return new SpeciesDTO(
        species.getId(),
        species.getName(),
        species.getWeightKg(),
        species.getHeightMeters()
    );
  }
}
