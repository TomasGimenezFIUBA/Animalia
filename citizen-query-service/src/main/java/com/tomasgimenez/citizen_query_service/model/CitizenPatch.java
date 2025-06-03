package com.tomasgimenez.citizen_query_service.model;

import java.util.List;
import java.util.Optional;

public record CitizenPatch(
    Optional<String> name,
    Optional<Boolean> hasHumanPet,
    Optional<Species> species,
    Optional<List<String>> roleNames
) {
}
