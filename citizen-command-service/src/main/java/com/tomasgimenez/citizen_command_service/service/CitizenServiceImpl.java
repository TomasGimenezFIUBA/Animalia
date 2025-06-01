package com.tomasgimenez.citizen_command_service.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_command_service.mapper.CitizenEventMapper;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.policy.role.RolePolicyValidator;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitizenServiceImpl implements CitizenService {
  private final CitizenRepository citizenRepository;
  private final SpeciesService speciesService;
  private final RoleService roleService;
  private final CitizenEventMapper citizenEventMapper;
  private final CitizenEventProducerService citizenEventProducerService;
  @Autowired @Setter
  private RolePolicyValidator rolePolicyValidator;

  @Override
  public CitizenEntity createCitizen(CreateCitizenRequest request) {
    log.info("Creating citizen with name: {}", request.name());

    var speciesEntity = speciesService.getById(request.speciesId());
    rolePolicyValidator.validate(request.roleNames(), Optional.empty());
    var roles = roleService.getRolesByRoleNames(request.roleNames());

    var entity = CitizenEntity.builder()
        .roles(roles)
        .name(request.name())
        .hasHumanPet(request.hasHumanPet())
        .species(speciesEntity)
        .build();

    var saved = citizenRepository.save(entity);
    citizenEventProducerService.sendCitizenCreatedEvent(
        citizenEventMapper.toCreatedEvent(saved)
    );
    log.info("Citizen created with ID: {}", saved.getId());

    return saved;
  }

  @Override
  public void updateCitizen(UpdateCitizenRequest request) {
    log.info("Updating citizen with ID: {}", request.id());

    var citizenEntity = citizenRepository.findById(request.id())
        .orElseThrow(() -> {
          log.warn("Citizen not found with ID: {}", request.id());
          return new EntityNotFoundException("Citizen not found with id: " + request.id());
        });

    if (request.name() != null) {
      citizenEntity.setName(request.name());
    }
    if (request.hasHumanPet() != null) {
      citizenEntity.setHasHumanPet(request.hasHumanPet());
    }
    if (request.speciesId() != null) {
      var speciesEntity = speciesService.getById(request.speciesId());
      citizenEntity.setSpecies(speciesEntity);
    }
    if (request.roleNames() != null) {
      rolePolicyValidator.validate(request.roleNames(), Optional.of(request.id()));
      var roles = roleService.getRolesByRoleNames(request.roleNames());
      citizenEntity.setRoles(roles);
    }

    citizenRepository.save(citizenEntity);
    citizenEventProducerService.sendCitizenUpdatedEvent(
        citizenEventMapper.toUpdatedEvent(citizenEntity)
    );
    log.info("Citizen with ID {} updated successfully", request.id());
  }

  @Override
  public void deleteCitizen(UUID id) {
    log.info("Deleting citizen with ID: {}", id);
    citizenRepository.deleteById(id);
    citizenEventProducerService.sendCitizenDeletedEvent(
        citizenEventMapper.toDeletedEvent(id)
    );
    log.info("Citizen with ID {} deleted", id);
  }

  @Override
  public Set<CitizenEntity> createCitizens(List<CreateCitizenRequest> requests) {
    log.info("Creating bulk citizens: total {}", requests.size());

    var speciesIds = requests.stream()
        .map(CreateCitizenRequest::speciesId)
        .collect(Collectors.toSet());
    var speciesEntities = speciesService.getByIds(speciesIds);

    rolePolicyValidator.validateBulk(
        requests.stream().map(CreateCitizenRequest::roleNames).toList()
    );

    var rolesEntities = roleService.getRolesByRoleNames(
        requests.stream().flatMap(r -> r.roleNames().stream()).collect(Collectors.toSet())
    );

    var entities = requests.stream()
        .map(request -> CitizenEntity.builder()
            .id(UUID.randomUUID()) // -> necessary for bulk creation
            .name(request.name())
            .hasHumanPet(request.hasHumanPet())
            .species(speciesEntities.stream()
                .filter(species -> species.getId().equals(request.speciesId()))
                .findFirst()
                .orElseThrow(() -> {
                  log.warn("Species not found for ID: {}", request.speciesId());
                  return new EntityNotFoundException("Species not found with ID: " + request.speciesId());
                }))
            .roles(rolesEntities.stream()
                .filter(role -> request.roleNames().contains(role.getName()))
                .collect(Collectors.toSet()))
            .build())
        .toList();

    var saved = citizenRepository.saveAll(entities);
    log.info("Bulk creation completed. Total created: {}", saved.size());

    return new HashSet<>(saved);
  }

  @Override
  public Set<CitizenEntity> getCitizensByRoleName(RoleName roleName) {
    log.info("Retrieving citizens by role names: {}", roleName);

    var citizens = citizenRepository.findByRoleName(roleName);
    return new HashSet<>(citizens);
  }

  public CitizenEntity getById(UUID id) {
    log.info("Retrieving citizen by ID: {}", id);

    return citizenRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Citizen not found with ID: {}", id);
          return new EntityNotFoundException("Citizen not found with id: " + id);
        });
  }
}

