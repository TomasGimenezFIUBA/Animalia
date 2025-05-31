package com.tomasgimenez.citizen_command_service.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;

public interface CitizenService {
  CitizenDTO createCitizen(CreateCitizenRequest request);
  void updateCitizen(UpdateCitizenRequest request);
  void deleteCitizen(UUID id);
  Set<CitizenDTO> createCitizens(List<CreateCitizenRequest> requests);
}
