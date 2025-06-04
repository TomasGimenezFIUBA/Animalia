package com.tomasgimenez.citizen_command_service.model.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

public record CitizenDTO(
    UUID id,
    String name,
    boolean hasHumanPet,
    SpeciesDTO speciesDTO,
    Set<RoleName> roleNames
) {
  public static CitizenDTO fromEntity(CitizenEntity citizen) {
    var species = SpeciesDTO.fromEntity(citizen.getSpecies());
    var roles = citizen.getRoleEntities().stream().map(
        RoleEntity::getName).collect(Collectors.toSet());
    return new CitizenDTO(citizen.getId(), citizen.getName(), citizen.isHasHumanPet(), species, roles);
  }
}
