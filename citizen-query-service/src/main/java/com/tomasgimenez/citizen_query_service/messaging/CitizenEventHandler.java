package com.tomasgimenez.citizen_query_service.messaging;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.citizen_query_service.exception.CorruptedCitizenDocumentException;
import com.tomasgimenez.citizen_query_service.exception.DuplicatedCitizenException;
import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
import com.tomasgimenez.citizen_query_service.model.Species;
import com.tomasgimenez.citizen_query_service.service.CitizenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CitizenEventHandler {

  private final CitizenService citizenService;

  public void handleCreated(CitizenCreatedEvent event) {
    validateRequired(event.getId(), "CitizenCreatedEvent", "id");
    validateRequired(event.getSpecies(), "CitizenCreatedEvent", "species");
    validateRoles(event.getRoleNames(), event.getId());

    var citizen = CitizenDocument.builder()
        .id(event.getId().toString())
        .name(event.getName().toString())
        .hasHumanPet(event.getHasHumanPet())
        .species(toDomainSpecies(event.getSpecies()))
        .roleNames(toStringList(event.getRoleNames()))
        .build();

    try {
      citizenService.insert(citizen);
    } catch (DuplicatedCitizenException e) {
      log.warn("Citizen with id {} already exists, skipping creation", event.getId());
    }

    log.debug("Citizen with id {} created successfully", event.getId());
  }

  public void handleUpdated(CitizenUpdatedEvent event) {
    validateRequired(event.getId(), "CitizenUpdatedEvent", "id");

    var citizen = new CitizenPatch(
        Optional.ofNullable(event.getName()).map(CharSequence::toString),
        Optional.ofNullable(event.getHasHumanPet()),
        Optional.ofNullable(event.getSpecies()).map(this::toDomainSpecies),
        Optional.ofNullable(event.getRoleNames()).filter(list -> !list.isEmpty()).map(this::toStringList)
    );

    citizenService.update(event.getId().toString(), citizen);
    log.debug("Citizen with id {} updated successfully", event.getId());
  }

  public void handleDeleted(CitizenDeletedEvent event) {
    validateRequired(event.getId(), "CitizenDeletedEvent", "id");

    citizenService.deleteById(event.getId().toString());
    log.debug("Citizen with id {} deleted successfully", event.getId());
  }

  private void validateRequired(Object value, String eventName, String fieldName) {
    if (value == null || value.toString().isEmpty()) {
      log.error("{} has no {}", eventName, fieldName);
      throw new CorruptedCitizenDocumentException(eventName + " has no " + fieldName);
    }
  }

  private void validateRoles(List<CharSequence> roles, CharSequence id) {
    if (roles == null || roles.isEmpty()) {
      log.error("CitizenCreatedEvent with id {} has no role names", id);
      throw new CorruptedCitizenDocumentException("CitizenCreatedEvent with id " + id + " has no role names");
    }
  }

  private Species toDomainSpecies(com.tomasgimenez.animalia.avro.Species avroSpecies) {
    var name = avroSpecies.getName().toString();
    var id = avroSpecies.getId().toString();
    return new Species(id, name, avroSpecies.getWeight(), avroSpecies.getHeight());
  }

  private List<String> toStringList(List<CharSequence> charSeqList) {
    return charSeqList.stream().map(CharSequence::toString).toList();
  }
}
