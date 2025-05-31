package com.tomasgimenez.citizen_command_service.controller;

import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.dto.SpeciesDTO;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.service.CitizenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitizenControllerTest {

  private CitizenService citizenService;
  private CitizenController citizenController;
  private CitizenDTO citizenDTO;
  @BeforeEach
  void setUp() {
    citizenService = mock(CitizenService.class);
    citizenController = new CitizenController(citizenService);

    citizenDTO = new CitizenDTO(UUID.randomUUID(), "Firulais", true, new SpeciesDTO(UUID.randomUUID(), "Dog",
        20.0, 200.0), Set.of(RoleName.CIVIL));
  }

  @Test
  void createCitizen_shouldReturnCreatedCitizen() {
    CreateCitizenRequest request = new CreateCitizenRequest(
        "Firulais",
        UUID.randomUUID(),
        true,
        Set.of(RoleName.CIVIL)
    );

    when(citizenService.createCitizen(request)).thenReturn(citizenDTO);

    ResponseEntity<CitizenDTO> response = citizenController.createCitizen(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(citizenDTO, response.getBody());
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
    Set<CitizenDTO> expectedSet = Set.of(citizenDTO, new CitizenDTO(
        UUID.randomUUID(), "Toby", false, new SpeciesDTO(UUID.randomUUID(), "Cat", 10.0, 100.0), Set.of(RoleName.CIVIL)
    ));

    when(citizenService.createCitizens(requestList)).thenReturn(expectedSet);

    ResponseEntity<Set<CitizenDTO>> response = citizenController.createCitizens(requestList);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expectedSet, response.getBody());
    verify(citizenService).createCitizens(requestList);
  }
}
