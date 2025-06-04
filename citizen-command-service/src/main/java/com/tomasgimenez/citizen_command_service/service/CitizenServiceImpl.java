package com.tomasgimenez.citizen_command_service.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomasgimenez.animalia.avro.CitizenEventEnvelope;
import com.tomasgimenez.animalia.avro.CitizenEventType;
import com.tomasgimenez.citizen_command_service.config.KafkaTopics;
import com.tomasgimenez.citizen_command_service.exception.EntityConflictException;
import com.tomasgimenez.citizen_command_service.exception.EntityPersistenceException;
import com.tomasgimenez.citizen_command_service.mapper.CitizenEventMapper;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.policy.role.RolePolicyValidator;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;
import com.tomasgimenez.citizen_command_service.repository.OutboxCitizenEventRepository;
import com.tomasgimenez.citizen_common.kafka.AvroSerializer;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PessimisticLockException;
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
  private final OutboxCitizenEventRepository outboxCitizenEventRepository;
  private final AvroSerializer avroSerializer;
  private final KafkaTopics kafkaTopics;
  @Setter
  private RolePolicyValidator rolePolicyValidator;

  @Transactional
  @Override
  public CitizenEntity createCitizen(CreateCitizenRequest request) {
    try {
      var speciesEntity = speciesService.getById(request.speciesId());
      rolePolicyValidator.validate(request.roleNames(), Optional.empty());
      var roles = roleService.getRolesByRoleNames(request.roleNames());

      var entity = CitizenEntity.builder()
          .roleEntities(roles)
          .name(request.name())
          .hasHumanPet(request.hasHumanPet())
          .species(speciesEntity)
          .build();

      var saved = citizenRepository.save(entity);
      createOutboxCitizenEvent(saved.getId(), citizenEventMapper.toCreatedEvent(saved), CitizenEventType.CREATED);

      log.debug("Citizen created with ID: {}", saved.getId());
      return saved;
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Persistence error creating citizen: {}", e.getMessage(), e);
      throw new EntityPersistenceException("Error while creating citizen", e);
    }
  }

  @Transactional
  @Override
  public void updateCitizen(UpdateCitizenRequest request) {
    try {
      var citizenOptional = citizenRepository.findByIdForUpdate(request.id());

      if (citizenOptional.isEmpty()) {
        log.warn("Citizen not found with ID: {}", request.id());
        throw new EntityNotFoundException("Citizen not found with id: " + request.id());
      }

      var citizenEntity = citizenOptional.get();

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
        var roleEntities = roleService.getRolesByRoleNames(request.roleNames());
        citizenEntity.setRoleEntities(roleEntities);
      }

      var saved = citizenRepository.save(citizenEntity);
      createOutboxCitizenEvent(citizenEntity.getId(), citizenEventMapper.toUpdatedEvent(saved), CitizenEventType.UPDATED);
      log.debug("Citizen with ID {} updated successfully", request.id());
    } catch (EntityNotFoundException e) {
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
      createOutboxCitizenEvent(id, citizenEventMapper.toDeletedEvent(id), CitizenEventType.DELETED);
      log.debug("Citizen with ID {} deleted", id);
    } catch (Exception e) {
      log.error("Persistence error deleting citizen: {}", e.getMessage(), e);
      throw new EntityPersistenceException("Error while deleting citizen", e);
    }
  }

  @Transactional
  @Override
  public Set<CitizenEntity> createCitizens(List<CreateCitizenRequest> requests) {
    try {
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

      var saved = citizenRepository.saveAll(entities);
      createOutboxCitizenEventBulk(saved);
      log.debug("Bulk creation completed. Total created: {}", saved.size());

      return new HashSet<>(saved);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Persistence error during bulk creation: {}", e.getMessage(), e);
      throw new EntityPersistenceException("Error while creating citizens", e);
    }
  }

  @Override
  public Set<CitizenEntity> getCitizensByRoleName(RoleName roleName) {
    var citizens = citizenRepository.findByRoleName(roleName);
    return new HashSet<>(citizens);
  }

  public CitizenEntity getById(UUID id) {
    return citizenRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Citizen not found with ID: {}", id);
          return new EntityNotFoundException("Citizen not found with id: " + id);
        });
  }

  private void createOutboxCitizenEvent(UUID citizenId, CitizenEventEnvelope event, CitizenEventType eventType) {
    var serializedEvent = avroSerializer.serialize(event);
    CitizenEventEntity outboxEvent = CitizenEventEntity.builder()
        .aggregateId(citizenId)
        .aggregateType("Citizen")
        .type(eventType.name())
        .payload(serializedEvent)
        .processed(false)
        .topic(kafkaTopics.getCitizenEvent())
        .createdAt(Instant.now())
        .build();
    outboxCitizenEventRepository.save(outboxEvent);
    log.debug("Outbox event created for citizen with ID: {}", citizenId);
  }

  private void createOutboxCitizenEventBulk(List<CitizenEntity> citizenEntities) {
    var outboxEvents = citizenEntities.stream()
        .map(citizen -> {
          var event = citizenEventMapper.toCreatedEvent(citizen);
          return CitizenEventEntity.builder()
              .id(UUID.randomUUID())
              .aggregateId(citizen.getId())
              .aggregateType("Citizen")
              .type(CitizenEventType.CREATED.name())
              .payload(avroSerializer.serialize(event))
              .processed(false)
              .createdAt(Instant.now())
              .topic(kafkaTopics.getCitizenEvent())
              .build();
        })
        .toList();

    outboxCitizenEventRepository.saveAll(outboxEvents);
    log.debug("Bulk outbox events created: total {}", outboxEvents.size());
  }
}
