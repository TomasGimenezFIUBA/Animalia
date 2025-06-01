package com.tomasgimenez.citizen_command_service.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;
import com.tomasgimenez.citizen_command_service.config.KafkaProperties;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

public interface CitizenService {
  CitizenEntity createCitizen(CreateCitizenRequest request);
  void updateCitizen(UpdateCitizenRequest request);
  void deleteCitizen(UUID id);
  Set<CitizenEntity> createCitizens(List<CreateCitizenRequest> requests);
  Set<CitizenEntity> getCitizensByRoleName(RoleName roleName);
}
