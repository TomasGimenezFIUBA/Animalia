package com.tomasgimenez.citizen_command_service.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

import java.util.Set;
import java.util.UUID;

public record CreateCitizenRequest(
    @NotBlank(message = "Name is required and cannot be blank")
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name,

    @NotNull(message = "Species ID is required")
    UUID speciesId,

    boolean hasHumanPet,

    @NotNull(message = "Role names are required")
    @Size(min = 1, message = "At least one role must be specified")
    Set<RoleName> roleNames
) {}
