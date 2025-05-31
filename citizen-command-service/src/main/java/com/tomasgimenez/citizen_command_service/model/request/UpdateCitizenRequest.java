package com.tomasgimenez.citizen_command_service.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

public record UpdateCitizenRequest(
    @NotNull(message = "Id is required")
    @JsonProperty("id")
    UUID id,

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    String name,

    UUID speciesId,

    Boolean hasHumanPet,

    @Size(min = 1, message = "At least one role must be provided")
    Set<RoleName> roleNames
) {}


