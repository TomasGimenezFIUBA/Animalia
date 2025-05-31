package com.tomasgimenez.citizen_command_service.controller;

import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.service.CitizenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/citizens")
public class CitizenController {

  private final CitizenService citizenService;

  public CitizenController(CitizenService citizenService) {
    this.citizenService = citizenService;
  }

  @Operation(summary = "Create a new citizen")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Citizen created"),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<CitizenDTO> createCitizen(
      @Valid @RequestBody
      CreateCitizenRequest request) {
    CitizenDTO citizen = citizenService.createCitizen(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(citizen);
  }

  @Operation(summary = "Update an existing citizen")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Citizen updated"),
      @ApiResponse(responseCode = "404", description = "Citizen not found", content = @Content)
  })
  @PutMapping
  public ResponseEntity<CitizenDTO> updateCitizen(
      @Valid @RequestBody
      UpdateCitizenRequest request) {
    citizenService.updateCitizen(request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete a citizen by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Citizen deleted"),
      @ApiResponse(responseCode = "404", description = "Citizen not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCitizen(@PathVariable UUID id) {
    citizenService.deleteCitizen(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Create multiple citizens at once")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Citizens created"),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/bulk")
  public ResponseEntity<Set<CitizenDTO>> createCitizens(
      @Valid @RequestBody
      List<CreateCitizenRequest> requests) {
    Set<CitizenDTO> citizens = citizenService.createCitizens(requests);
    return ResponseEntity.status(HttpStatus.CREATED).body(citizens);
  }
}

