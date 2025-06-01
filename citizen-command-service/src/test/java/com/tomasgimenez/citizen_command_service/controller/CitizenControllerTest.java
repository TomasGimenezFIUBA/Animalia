package com.tomasgimenez.citizen_command_service.controller;

import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.service.CitizenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitizenControllerTest {

  private CitizenService citizenService;
  private CitizenController citizenController;
  private CitizenEntity citizen;

  @BeforeEach
  void setUp() {
    citizenService = mock(CitizenService.class);
    citizenController = new CitizenController(citizenService);

    citizen = CitizenEntity.builder()
        .id(UUID.randomUUID())
        .name("Fido")
        .species(new SpeciesEntity(UUID.randomUUID(), "Dog", 15.0, 200.0))
        .roles(Set.of(new RoleEntity(UUID.randomUUID(), RoleName.CIVIL)))
        .build();
  }

  @Test
  void createCitizen_shouldReturnCreatedCitizen() {
    CreateCitizenRequest request = new CreateCitizenRequest(
        "Firulais",
        UUID.randomUUID(),
        true,
        Set.of(RoleName.CIVIL)
    );

    when(citizenService.createCitizen(request)).thenReturn(citizen);

    ResponseEntity<CitizenDTO> response = citizenController.createCitizen(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(CitizenDTO.fromEntity(citizen), response.getBody());
    verify(citizenService).createCitizen(request);
  }

  @Test
  void updateCitizen_shouldReturnNoContent() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(
        UUID.randomUUID(),
        "Whiskers",
        UUID.randomUUID(),
        false,
        Set.of(RoleName.MINISTER_OF_STATE)
    );

    ResponseEntity<CitizenDTO> response = citizenController.updateCitizen(request);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(citizenService).updateCitizen(request);
  }

  @Test
  void deleteCitizen_shouldReturnNoContent() {
    UUID id = UUID.randomUUID();

    ResponseEntity<Void> response = citizenController.deleteCitizen(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(citizenService).deleteCitizen(id);
  }

  @Test
  void createCitizens_shouldReturnCreatedCitizens() {
    CreateCitizenRequest req1 = new CreateCitizenRequest(
        "Luna",
        UUID.randomUUID(),
        true,
        Set.of(RoleName.GENERAL)
    );

    CreateCitizenRequest req2 = new CreateCitizenRequest(
        "Toby",
        UUID.randomUUID(),
        false,
        Set.of(RoleName.CIVIL)
    );

    List<CreateCitizenRequest> requestList = List.of(req1, req2);

    Set<CitizenEntity> entitySet = Set.of(
        CitizenEntity.builder()
            .id(UUID.randomUUID())
            .name("Luna")
            .species(new SpeciesEntity(UUID.randomUUID(), "Dog", 15.0, 200.0))
            .roles(Set.of(new RoleEntity(UUID.randomUUID(), RoleName.GENERAL)))
            .build(),
        CitizenEntity.builder()
            .id(UUID.randomUUID())
            .name("Toby")
            .species(new SpeciesEntity(UUID.randomUUID(), "Cat", 10.0, 100.0))
            .roles(Set.of(new RoleEntity(UUID.randomUUID(), RoleName.CIVIL)))
            .build()
    );
    var expectedSet = entitySet.stream()
            .map(CitizenDTO::fromEntity)
            .collect(Collectors.toSet());

    when(citizenService.createCitizens(requestList)).thenReturn(entitySet);

    ResponseEntity<Set<CitizenDTO>> response = citizenController.createCitizens(requestList);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expectedSet, response.getBody());
    verify(citizenService).createCitizens(requestList);
  }
}
