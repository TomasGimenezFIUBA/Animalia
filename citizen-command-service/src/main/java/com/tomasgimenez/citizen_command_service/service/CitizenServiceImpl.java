package com.tomasgimenez.citizen_command_service.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomasgimenez.citizen_command_service.exception.EntityConflictException;
import com.tomasgimenez.citizen_command_service.exception.EntityPersistenceException;
import com.tomasgimenez.citizen_command_service.exception.InvalidEntityReferenceException;
import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.policy.role.RolePolicyValidator;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;
import com.tomasgimenez.citizen_common.exception.DatabaseAccessException;

import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitizenServiceImpl implements CitizenService {
  private final CitizenRepository citizenRepository;
  private final SpeciesService speciesService;
  private final RoleService roleService;
  private final RolePolicyValidator rolePolicyValidator;
  private final CitizenEventService citizenEventService;

  @Transactional
  @Override
  public CitizenEntity createCitizen(CreateCitizenRequest request) {
    rolePolicyValidator.validate(request.roleNames(), Optional.empty());

    return handleCreation("creating citizen", () -> {
      var speciesEntity = speciesService.getById(request.speciesId());
      var roles = roleService.getRolesByRoleNames(request.roleNames());

      var entity = CitizenEntity.builder()
          .roleEntities(roles)
          .name(request.name())
          .hasHumanPet(request.hasHumanPet())
          .species(speciesEntity)
          .build();

      var saved = citizenRepository.save(entity);
      citizenEventService.createCreatedEvent(saved);
      log.debug("Citizen created with ID: {}", saved.getId());
      return saved;
    });
  }

  @Transactional
  @Override
  public void updateCitizen(UpdateCitizenRequest request) {
    try {
      var citizenEntity = citizenRepository.findByIdForUpdate(request.id()).orElseThrow(
          () -> {
            log.warn("Citizen not found with ID: {}", request.id());
            return new EntityNotFoundException("Citizen not found with id: " + request.id());
          }
      );

      applyUpdatedFieldsToCitizenEntity(request, citizenEntity);

      var saved = citizenRepository.save(citizenEntity);
      citizenEventService.createUpdatedEvent(saved);
      log.debug("Citizen with ID {} updated successfully", request.id());
    } catch (EntityNotFoundException | InvalidEntityReferenceException | RolePolicyException e) {
      throw e;
    } catch (PessimisticLockException | DataIntegrityViolationException e) {
      log.error("Conflict updating citizen: {}", e.getMessage(), e);
      throw new EntityConflictException("Conflict occurred while updating citizen", e);
    } catch (Exception e) {
      log.error("Persistence error updating citizen: {}", e.getMessage(), e);
      throw new EntityPersistenceException("Error while updating citizen", e);
    }
  }

  @Transactional
  @Override
  public void deleteCitizen(UUID id) {
    try {
      citizenRepository.deleteById(id);
      citizenEventService.createDeletedEvent(id);
      log.debug("Citizen with ID {} deleted", id);
    } catch (EntityPersistenceException e) {
      throw e;
    } catch (PessimisticLockException | DataIntegrityViolationException e) {
      log.error("Conflict deleting citizen: {}", e.getMessage(), e);
      throw new EntityConflictException("Conflict occurred while deleting citizen", e);
    } catch (Exception e) {
      log.error("Persistence error deleting citizen: {}", e.getMessage(), e);
      throw new EntityPersistenceException("Error while deleting citizen", e);
    }
  }

  @Transactional
  @Override
  public Set<CitizenEntity> createCitizens(List<CreateCitizenRequest> requests) {
    rolePolicyValidator.validateBulk(
        requests.stream().map(CreateCitizenRequest::roleNames).toList()
    );

    return handleCreation("creating citizens in bulk", () -> {
      var speciesIds = requests.stream()
          .map(CreateCitizenRequest::speciesId)
          .collect(Collectors.toSet());
      var speciesEntities = speciesService.getByIds(speciesIds);

      var rolesEntities = roleService.getRolesByRoleNames(
          requests.stream().flatMap(r -> r.roleNames().stream()).collect(Collectors.toSet())
      );

      var entities = createCitizenEntitiesFromRequests(requests, speciesEntities, rolesEntities);

      var saved = citizenRepository.saveAll(entities);
      saved.forEach(citizenEventService::createCreatedEvent);
      log.debug("Bulk creation completed. Total created: {}", saved.size());
      return new HashSet<>(saved);
    });
  }

  @Override
  public Set<CitizenEntity> getCitizensByRoleName(RoleName roleName) {
    try {
      return new HashSet<>(citizenRepository.findByRoleName(roleName));
    } catch (Exception e) {
      log.error("Error fetching citizens by role name: {}", roleName, e);
      throw new DatabaseAccessException("Error accessing database for citizens with role name: " + roleName, e);
    }
  }

  public CitizenEntity getById(UUID id) {
    Optional<CitizenEntity> optionalCitizen;
    try {
      optionalCitizen = citizenRepository.findById(id);
    } catch (Exception e) {
      log.error("Error fetching citizen by ID: {}", id, e);
      throw new DatabaseAccessException("Error accessing database for citizen with ID: " + id, e);
    }

    return optionalCitizen.orElseThrow(() -> {
      log.warn("Citizen not found for ID: {}", id);
      return new EntityNotFoundException("Citizen not found with ID: " + id);
    });
  }

  private List<CitizenEntity> createCitizenEntitiesFromRequests(List<CreateCitizenRequest> requests,
      Set<SpeciesEntity> speciesEntities, Set<RoleEntity> rolesEntities) {
    return requests.stream()
        .map(request -> CitizenEntity.builder()
            .id(UUID.randomUUID())
            .name(request.name())
            .hasHumanPet(request.hasHumanPet())
            .species(speciesEntities.stream()
                .filter(species -> species.getId().equals(request.speciesId()))
                .findFirst()
                .orElseThrow(() -> {
                  log.warn("Species not found for ID: {}", request.speciesId());
                  return new EntityNotFoundException("Species not found with ID: " + request.speciesId());
                }))
            .roleEntities(rolesEntities.stream()
                .filter(role -> request.roleNames().contains(role.getName()))
                .collect(Collectors.toSet()))
            .build())
        .toList();
  }

  private void applyUpdatedFieldsToCitizenEntity(UpdateCitizenRequest request, CitizenEntity citizenEntity) {
    if (request.name() != null) {
      citizenEntity.setName(request.name());
    }
    if (request.hasHumanPet() != null) {
      citizenEntity.setHasHumanPet(request.hasHumanPet());
    }
    try {
      if (request.speciesId() != null) {
        var speciesEntity = speciesService.getById(request.speciesId());
        citizenEntity.setSpecies(speciesEntity);
      }
      if (request.roleNames() != null) {
        rolePolicyValidator.validate(request.roleNames(), Optional.of(request.id()));
        var roleEntities = roleService.getRolesByRoleNames(request.roleNames());
        citizenEntity.setRoleEntities(roleEntities);
      }
    } catch (EntityNotFoundException e){
      log.error("Invalid reference in update request: {}", e.getMessage(), e);
      throw new InvalidEntityReferenceException(
          "Invalid reference in update request: " + e.getMessage());
    }
  }

  private <T> T handleCreation(String operationName, Supplier<T> operation) {
    try {
      return operation.get();
    } catch (EntityPersistenceException | DatabaseAccessException e) {
      throw e;
    } catch (EntityNotFoundException e) {
      throw new InvalidEntityReferenceException("Invalid reference in request during " + operationName + ": " + e.getMessage());
    } catch (Exception e) {
      log.error("Persistence error during {}: {}", operationName, e.getMessage(), e);
      throw new EntityPersistenceException("Error while " + operationName, e);
    }
  }

}
