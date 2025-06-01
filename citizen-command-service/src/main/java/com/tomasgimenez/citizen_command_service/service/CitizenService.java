package com.tomasgimenez.citizen_command_service.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;

public interface CitizenService {
  CitizenEntity createCitizen(CreateCitizenRequest request);
  void updateCitizen(UpdateCitizenRequest request);
  void deleteCitizen(UUID id);
  Set<CitizenEntity> createCitizens(List<CreateCitizenRequest> requests);
  Set<CitizenEntity> getCitizensByRoleName(RoleName roleName);
}
