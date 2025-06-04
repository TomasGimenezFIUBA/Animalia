package com.tomasgimenez.citizen_command_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.config.KafkaTopics;
import com.tomasgimenez.citizen_command_service.exception.EntityConflictException;
import com.tomasgimenez.citizen_command_service.exception.EntityPersistenceException;
import com.tomasgimenez.citizen_command_service.mapper.CitizenEventMapper;
import com.tomasgimenez.citizen_command_service.model.entity.*;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.policy.role.RolePolicyValidator;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;
import com.tomasgimenez.citizen_command_service.repository.OutboxCitizenEventRepository;
import com.tomasgimenez.citizen_common.kafka.AvroSerializer;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PessimisticLockException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

class CitizenServiceImplTest {

  private CitizenRepository citizenRepository;
  private SpeciesService speciesService;
  private RolePolicyValidator rolePolicyValidator;
  private RoleService roleService;
  private CitizenServiceImpl citizenService;
  private OutboxCitizenEventRepository outboxCitizenEventRepository;
  private CitizenEntity citizen;
  private SpeciesEntity species;
  private RoleEntity role;

  @BeforeEach
  void setUp() {
    citizenRepository = mock(CitizenRepository.class);
    speciesService = mock(SpeciesService.class);
    rolePolicyValidator = mock(RolePolicyValidator.class);
    roleService = mock(RoleService.class);
    CitizenEventMapper citizenEventMapper = new CitizenEventMapper();
    citizenEventMapper.setSource("test-source");
    outboxCitizenEventRepository = mock(OutboxCitizenEventRepository.class);
    AvroSerializer avroSerializer = mock(AvroSerializer.class);
    KafkaTopics kafkaTopics = mock(KafkaTopics.class);

    citizenService = new CitizenServiceImpl(
        citizenRepository, speciesService, roleService, citizenEventMapper,
        outboxCitizenEventRepository, avroSerializer, kafkaTopics
    );
    citizenService.setRolePolicyValidator(rolePolicyValidator);

    species = new SpeciesEntity(UUID.randomUUID(), "Lion", 190.0, 1.2);
    role = new RoleEntity(UUID.randomUUID(), RoleName.CIVIL);
    citizen = new CitizenEntity(UUID.randomUUID(), "Leo", true, species, Set.of(role));
  }
  @Test
  void createCitizen_shouldCreateSuccessfully() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    when(speciesService.getById(species.getId())).thenReturn(species);
    when(roleService.getRolesByRoleNames(roles)).thenReturn(Set.of(role));
    when(citizenRepository.save(any())).thenReturn(citizen);

    citizenService.createCitizen(request);

    verify(rolePolicyValidator).validate(roles, Optional.empty());
    verify(citizenRepository).save(any());
    verify(outboxCitizenEventRepository).save(any(CitizenEventEntity.class));
  }

  @Test
  void createCitizens_shouldCreateAllSuccessfully() {
    UUID s1 = UUID.randomUUID();
    UUID s2 = UUID.randomUUID();
    Set<RoleName> r1 = Set.of(RoleName.CIVIL);
    Set<RoleName> r2 = Set.of(RoleName.GENERAL);

    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, r1),
        new CreateCitizenRequest("B", s2, false, r2)
    );

    SpeciesEntity spec1 = new SpeciesEntity(s1, "s1", 12.0, 12.0);
    SpeciesEntity spec2 = new SpeciesEntity(s2, "s2", 12.0, 12.0);

    RoleEntity civ = new RoleEntity(UUID.randomUUID(), RoleName.CIVIL);
    RoleEntity gen = new RoleEntity(UUID.randomUUID(), RoleName.GENERAL);

    when(speciesService.getByIds(Set.of(s1, s2))).thenReturn(Set.of(spec1, spec2));
    when(roleService.getRolesByRoleNames(Set.of(RoleName.CIVIL, RoleName.GENERAL)))
        .thenReturn(Set.of(civ, gen));

    when(citizenRepository.saveAll(any())).thenAnswer(inv -> inv.getArguments()[0]);

    Set<CitizenEntity> result = citizenService.createCitizens(requests);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("A")));
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("B")));
    verify(rolePolicyValidator).validateBulk(List.of(r1, r2));
    verify(citizenRepository).saveAll(any());
    verify(outboxCitizenEventRepository).saveAll(any(List.class));
  }

  @Test
  void getCitizensByRoleName_shouldReturnCitizensSuccessfully() {
    CitizenEntity citizen2 = new CitizenEntity(
        UUID.randomUUID(), "Jane Doe", false, species, Set.of(role)
    );

    when(citizenRepository.findByRoleName(role.getName())).thenReturn(List.of(citizen, citizen2));

    Set<CitizenEntity> result = citizenService.getCitizensByRoleName(role.getName());

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(c -> c.getName().equals(citizen.getName())));
    assertTrue(result.stream().anyMatch(c -> c.getName().equals(citizen2.getName())));
    verify(citizenRepository).findByRoleName(role.getName());
  }

  @Test
  void getCitizensByRoleName_shouldReturnEmptySetWhenNoCitizensFound() {
    RoleName roleName = RoleName.CIVIL;

    when(citizenRepository.findByRoleName(roleName)).thenReturn(Collections.emptyList());

    Set<CitizenEntity> result = citizenService.getCitizensByRoleName(roleName);

    assertTrue(result.isEmpty());
    verify(citizenRepository).findByRoleName(roleName);
  }

  @Test
  void getById_shouldReturnCitizenSuccessfully() {
    UUID id = UUID.randomUUID();

    CitizenEntity citizenEntity = new CitizenEntity();
    citizenEntity.setId(id);
    citizenEntity.setName("John Doe");

    when(citizenRepository.findById(id)).thenReturn(Optional.of(citizenEntity));

    CitizenEntity result = citizenService.getById(id);

    assertEquals("John Doe", result.getName());
    assertEquals(id, result.getId());
    verify(citizenRepository).findById(id);
  }

  @Test
  void getById_shouldThrowWhenCitizenNotFound() {
    UUID id = UUID.randomUUID();

    when(citizenRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> citizenService.getById(id));

    assertTrue(exception.getMessage().contains("Citizen not found with id: " + id));
    verify(citizenRepository).findById(id);
  }

  @Test
  void deleteCitizen_shouldCallRepository() {
    UUID id = UUID.randomUUID();
    citizenService.deleteCitizen(id);
    verify(citizenRepository).deleteById(id);
    verify(outboxCitizenEventRepository).save(any(CitizenEventEntity.class));
  }

  @Test
  void updateCitizen_shouldUpdateFieldsSuccessfully() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), "Milo", null, true, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(citizenRepository.save(any(CitizenEntity.class))).thenReturn(citizen);

    citizenService.updateCitizen(request);

    assertEquals(request.name(), citizen.getName());
    assertTrue(citizen.isHasHumanPet());
    verify(citizenRepository).save(citizen);
    verify(rolePolicyValidator, never()).validate(any(), any());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(speciesService, never()).getById(any());
    verify(outboxCitizenEventRepository).save(any(CitizenEventEntity.class));
  }

  @Test
  void updateCitizen_shouldUpdateHasHumanPetSuccessfully() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), null, null, true, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(citizenRepository.save(citizen)).thenReturn(citizen);

    citizenService.updateCitizen(request);

    assertTrue(citizen.isHasHumanPet());
    verify(citizenRepository).save(citizen);
    verify(rolePolicyValidator, never()).validate(any(), any());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(speciesService, never()).getById(any());
    verify(outboxCitizenEventRepository).save(any(CitizenEventEntity.class));
  }

  @Test
  void updateCitizen_shouldUpdateSpeciesSuccessfully() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), null, species.getId(), null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(speciesService.getById(species.getId())).thenReturn(species);
    when(citizenRepository.save(citizen)).thenReturn(citizen);

    citizenService.updateCitizen(request);

    assertEquals(species, citizen.getSpecies());
    verify(citizenRepository).save(citizen);
    verify(rolePolicyValidator, never()).validate(any(), any());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(speciesService).getById(species.getId());
  }

  @Test
  void updateCitizen_shouldUpdateRolesSuccessfully() {
    Set<RoleName> roleNames = Set.of(role.getName());
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), null, null, null, roleNames);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(roleService.getRolesByRoleNames(roleNames)).thenReturn(Set.of(role));
    when(citizenRepository.save(any(CitizenEntity.class))).thenReturn(citizen);

    citizenService.updateCitizen(request);

    assertEquals(Set.of(role), citizen.getRoleEntities());
    verify(rolePolicyValidator).validate(roleNames, Optional.of(citizen.getId()));
    verify(citizenRepository).save(citizen);
    verify(roleService).getRolesByRoleNames(roleNames);
    verify(speciesService, never()).getById(any());
    verify(outboxCitizenEventRepository).save(any(CitizenEventEntity.class));
  }

  @Test
  void updateCitizen_shouldThrowIfNotFound() {
    UUID id = UUID.randomUUID();
    when(citizenRepository.findById(id)).thenReturn(Optional.empty());

    var req = new UpdateCitizenRequest(id, "Name", null, null, null);

    assertThrows(EntityNotFoundException.class, () -> citizenService.updateCitizen(req));
  }

  @Test
  void createCitizens_shouldThrowIfSpeciesMissing() {
    UUID s1 = UUID.randomUUID();
    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, Set.of(RoleName.CIVIL))
    );

    when(speciesService.getByIds(Set.of(s1))).thenReturn(Set.of());

    assertThrows(EntityNotFoundException.class,
        () -> citizenService.createCitizens(requests));
  }

  @Test
  void createCitizen_shouldThrowOnRepositoryError() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    when(speciesService.getById(species.getId())).thenReturn(species);
    when(roleService.getRolesByRoleNames(roles)).thenReturn(Set.of(role));
    when(citizenRepository.save(any())).thenThrow(new DataAccessException("DB error") {});

    assertThrows(EntityPersistenceException.class, () -> citizenService.createCitizen(request));
  }

  @Test
  void createCitizens_shouldThrowOnRepositoryError() {
    UUID s1 = UUID.randomUUID();
    Set<RoleName> roles = Set.of(RoleName.CIVIL);
    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, roles)
    );
    SpeciesEntity spec = new SpeciesEntity(s1, "s", 1.0, 1.0);
    RoleEntity civ = new RoleEntity(UUID.randomUUID(), RoleName.CIVIL);

    when(speciesService.getByIds(Set.of(s1))).thenReturn(Set.of(spec));
    when(roleService.getRolesByRoleNames(Set.of(RoleName.CIVIL))).thenReturn(Set.of(civ));
    when(citizenRepository.saveAll(any())).thenThrow(new DataAccessException("DB error") {});

    assertThrows(EntityPersistenceException.class, () -> citizenService.createCitizens(requests));
  }

  @Test
  void getCitizensByRoleName_shouldThrowOnRepositoryError() {
    when(citizenRepository.findByRoleName(role.getName()))
        .thenThrow(new DataAccessException("DB error") {});
    assertThrows(DataAccessException.class, () -> citizenService.getCitizensByRoleName(role.getName()));
  }

  @Test
  void deleteCitizen_shouldThrowOnRepositoryError() {
    UUID id = UUID.randomUUID();
    doThrow(new DataAccessException("DB error") {}).when(citizenRepository).deleteById(id);
    assertThrows(EntityPersistenceException.class, () -> citizenService.deleteCitizen(id));
  }

  @Test
  void updateCitizen_shouldThrowDataIntegrityViolationException() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), "Name", null, null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(citizenRepository.save(any())).thenThrow(new DataIntegrityViolationException("Invalid data"));

    assertThrows(EntityConflictException.class, () -> citizenService.updateCitizen(request));
  }

  @Test
  void updateCitizen_shouldThrowPessimisticLockException() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), "Name", null, null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId()))
        .thenThrow(new PessimisticLockException("", null, ""));

    assertThrows(EntityConflictException.class, () -> citizenService.updateCitizen(request));
  }
}
