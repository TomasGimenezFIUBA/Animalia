package com.tomasgimenez.citizen_command_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.exception.EntityConflictException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;
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

import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;

import jakarta.persistence.PessimisticLockException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class CitizenServiceImplTest {

  private CitizenRepository citizenRepository;
  private SpeciesService speciesService;
  private RolePolicyValidator rolePolicyValidator;
  private RoleService roleService;
  private CitizenServiceImpl citizenService;
  private CitizenEventService citizenEventService;
  private CitizenEntity citizen;
  private SpeciesEntity species;
  private RoleEntity role;

  @BeforeEach
  void setUp() {
    citizenRepository = mock(CitizenRepository.class);
    speciesService = mock(SpeciesService.class);
    rolePolicyValidator = mock(RolePolicyValidator.class);
    roleService = mock(RoleService.class);
    citizenEventService = mock(CitizenEventService.class);

    citizenService = new CitizenServiceImpl(
        citizenRepository, speciesService, roleService,
        rolePolicyValidator, citizenEventService
    );

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
    verify(citizenEventService, times(1)).createCreatedEvent(citizen);
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
    verify(rolePolicyValidator, times(1)).validateBulk(List.of(r1, r2));
    verify(citizenRepository, times(1)).saveAll(any());
    verify(citizenEventService, times(requests.size())).createCreatedEvent(any());
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

    assertThrows(EntityNotFoundException.class,
        () -> citizenService.getById(id));

    verify(citizenRepository, times(1)).findById(id);
  }

  @Test
  void getById_shouldThrowDatabaseAccessExceptionWhenRepositoryThrowsException() {
    UUID id = UUID.randomUUID();

    when(citizenRepository.findById(id)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseReadException exception = assertThrows(DatabaseReadException.class,
        () -> citizenService.getById(id));

    assertTrue(exception.getMessage().contains("Error accessing database for citizen with ID: " + id));
    verify(citizenRepository, times(1)).findById(id);
  }

  @Test
  void deleteCitizen_shouldCallRepositoryAndCitizenEventService() {
    UUID id = UUID.randomUUID();
    citizenService.deleteCitizen(id);
    verify(citizenRepository).deleteById(id);
    verify(citizenEventService, times(1)).createDeletedEvent(id);
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
    verify(citizenEventService, times(1)).createUpdatedEvent(citizen);
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
    verify(citizenEventService, times(1)).createUpdatedEvent(citizen);
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
    verify(citizenEventService, times(1)).createUpdatedEvent(citizen);
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

    assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.createCitizens(requests));
  }

  @Test
  void createCitizen_shouldThrowOnRepositoryError() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    when(speciesService.getById(species.getId())).thenReturn(species);
    when(roleService.getRolesByRoleNames(roles)).thenReturn(Set.of(role));
    when(citizenRepository.save(any())).thenThrow(new RuntimeException("DB error") {});

    assertThrows(DatabaseWriteException.class, () -> citizenService.createCitizen(request));
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
    when(citizenRepository.saveAll(any())).thenThrow(new RuntimeException("DB error") {});

    assertThrows(DatabaseWriteException.class, () -> citizenService.createCitizens(requests));
  }

  @Test
  void getCitizensByRoleName_shouldThrowExceptionOnRepositoryError() {
    when(citizenRepository.findByRoleName(role.getName())).thenThrow(new RuntimeException("DB error") {});

    assertThrows(DatabaseReadException.class, () -> citizenService.getCitizensByRoleName(role.getName()));
  }

  @Test
  void deleteCitizen_shouldThrowOnRepositoryError() {
    UUID id = UUID.randomUUID();
    doThrow(new RuntimeException("DB error") {}).when(citizenRepository).deleteById(id);
    assertThrows(DatabaseWriteException.class, () -> citizenService.deleteCitizen(id));
  }

  @Test
  void updateCitizen_shouldThrowDataIntegrityViolationException() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), "Name", null, null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(citizenRepository.save(any())).thenThrow(new DataIntegrityViolationException("Invalid data"));

    assertThrows(EntityConflictException.class, () -> citizenService.updateCitizen(request));
    verify(citizenEventService, never()).createUpdatedEvent(any());
  }

  @Test
  void updateCitizen_shouldThrowPessimisticLockException() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), "Name", null, null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId()))
        .thenThrow(new PessimisticLockException("", null, ""));

    assertThrows(EntityConflictException.class, () -> citizenService.updateCitizen(request));
    verify(citizenEventService, never()).createUpdatedEvent(any());
  }

  @Test
  void createCitizen_shouldPropagateRolePolicyException() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    doThrow(new RolePolicyException("Invalid role policy"))
        .when(rolePolicyValidator).validate(roles, Optional.empty());

    RolePolicyException exception = assertThrows(RolePolicyException.class,
        () -> citizenService.createCitizen(request));

    assertEquals("Invalid role policy", exception.getMessage());
    verify(rolePolicyValidator).validate(roles, Optional.empty());
    verify(citizenRepository, never()).save(any());
  }

  @Test
  void updateCitizen_shouldPropagateRolePolicyException() {
    Set<RoleName> roles = Set.of(role.getName());
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), null, null, null, roles);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    doThrow(new RolePolicyException("Invalid role policy"))
        .when(rolePolicyValidator).validate(roles, Optional.of(citizen.getId()));

    RolePolicyException exception = assertThrows(RolePolicyException.class,
        () -> citizenService.updateCitizen(request));

    assertEquals("Invalid role policy", exception.getMessage());
    verify(rolePolicyValidator).validate(roles, Optional.of(citizen.getId()));
    verify(citizenRepository, never()).save(any());
  }

  @Test
  void createCitizens_shouldPropagateRolePolicyException() {
    UUID s1 = UUID.randomUUID();
    Set<RoleName> r1 = Set.of(RoleName.CIVIL);
    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, r1)
    );

    doThrow(new RolePolicyException("Invalid role policy"))
        .when(rolePolicyValidator).validateBulk(List.of(r1));

    RolePolicyException exception = assertThrows(RolePolicyException.class,
        () -> citizenService.createCitizens(requests));

    assertEquals("Invalid role policy", exception.getMessage());
    verify(rolePolicyValidator).validateBulk(List.of(r1));
    verify(citizenRepository, never()).saveAll(any());
  }

  @Test
  void createCitizen_shouldThrowInvalidEntityReferenceExceptionWhenSpeciesNotFound() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    when(speciesService.getById(species.getId()))
        .thenThrow(new EntityNotFoundException("Species not found"));

    assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.createCitizen(request));

    verify(speciesService).getById(species.getId());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(citizenRepository, never()).save(any());
  }

  @Test
  void createCitizen_shouldThrowInvalidEntityReferenceExceptionWhenRoleNotFound() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    when(speciesService.getById(species.getId())).thenReturn(species);
    when(roleService.getRolesByRoleNames(roles))
        .thenThrow(new EntityNotFoundException("Role not found"));

    assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.createCitizen(request));

    verify(speciesService).getById(species.getId());
    verify(roleService).getRolesByRoleNames(roles);
    verify(citizenRepository, never()).save(any());
  }

  @Test
  void updateCitizen_shouldThrowInvalidEntityReferenceExceptionWhenSpeciesNotFound() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), null, species.getId(), null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(speciesService.getById(species.getId()))
        .thenThrow(new EntityNotFoundException("Species not found"));

    InvalidEntityReferenceException exception = assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.updateCitizen(request));

    assertTrue(exception.getMessage().contains("Invalid reference in update request: Species not found"));
    verify(speciesService).getById(species.getId());
    verify(citizenRepository, never()).save(any());
  }

  @Test
  void updateCitizen_shouldThrowInvalidEntityReferenceExceptionWhenRoleNotFound() {
    Set<RoleName> roles = Set.of(role.getName());
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), null, null, null, roles);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(roleService.getRolesByRoleNames(roles))
        .thenThrow(new EntityNotFoundException("Role not found"));

    InvalidEntityReferenceException exception = assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.updateCitizen(request));

    assertTrue(exception.getMessage().contains("Invalid reference in update request: Role not found"));
    verify(roleService).getRolesByRoleNames(roles);
    verify(citizenRepository, never()).save(any());
  }

  @Test
  void createCitizens_shouldThrowInvalidEntityReferenceExceptionWhenSpeciesNotFound() {
    UUID s1 = UUID.randomUUID();
    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, Set.of(RoleName.CIVIL))
    );

    when(speciesService.getByIds(Set.of(s1)))
        .thenThrow(new EntityNotFoundException("Species not found"));

    assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.createCitizens(requests));

    verify(speciesService).getByIds(Set.of(s1));
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(citizenRepository, never()).saveAll(any());
  }

  @Test
  void createCitizens_shouldThrowInvalidEntityReferenceExceptionWhenRoleNotFound() {
    UUID s1 = UUID.randomUUID();
    Set<RoleName> roles = Set.of(RoleName.CIVIL);
    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, roles)
    );

    SpeciesEntity spec = new SpeciesEntity(s1, "s", 1.0, 1.0);
    when(speciesService.getByIds(Set.of(s1))).thenReturn(Set.of(spec));
    when(roleService.getRolesByRoleNames(roles))
        .thenThrow(new EntityNotFoundException("Role not found"));

    assertThrows(InvalidEntityReferenceException.class,
        () -> citizenService.createCitizens(requests));

    verify(speciesService).getByIds(Set.of(s1));
    verify(roleService).getRolesByRoleNames(roles);
    verify(citizenRepository, never()).saveAll(any());
  }

  @Test
  void createCitizen_shouldThrowEntityPersistenceExceptionOnRepositoryError() {
    Set<RoleName> roles = Set.of(role.getName());
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", species.getId(), true, roles);

    when(speciesService.getById(species.getId())).thenReturn(species);
    when(roleService.getRolesByRoleNames(roles)).thenReturn(Set.of(role));
    when(citizenRepository.save(any())).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> citizenService.createCitizen(request));

    assertTrue(exception.getMessage().contains("Error while creating citizen"));
    verify(citizenRepository).save(any());
  }

  @Test
  void createCitizens_shouldThrowEntityPersistenceExceptionOnRepositoryError() {
    UUID s1 = UUID.randomUUID();
    Set<RoleName> roles = Set.of(RoleName.CIVIL);
    List<CreateCitizenRequest> requests = List.of(
        new CreateCitizenRequest("A", s1, true, roles)
    );

    SpeciesEntity spec = new SpeciesEntity(s1, "Species", 1.0, 1.0);
    RoleEntity civ = new RoleEntity(UUID.randomUUID(), RoleName.CIVIL);

    when(speciesService.getByIds(Set.of(s1))).thenReturn(Set.of(spec));
    when(roleService.getRolesByRoleNames(roles)).thenReturn(Set.of(civ));
    when(citizenRepository.saveAll(any())).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> citizenService.createCitizens(requests));

    assertTrue(exception.getMessage().contains("Error while creating citizens"));
    verify(citizenRepository).saveAll(any());
  }

  @Test
  void updateCitizen_shouldThrowEntityPersistenceExceptionOnRepositoryError() {
    UpdateCitizenRequest request = new UpdateCitizenRequest(citizen.getId(), "Updated Name", null, null, null);

    when(citizenRepository.findByIdForUpdate(citizen.getId())).thenReturn(Optional.of(citizen));
    when(citizenRepository.save(any())).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> citizenService.updateCitizen(request));

    assertTrue(exception.getMessage().contains("Error while updating citizen"));
    verify(citizenRepository).save(any());
  }

  @Test
  void deleteCitizen_shouldThrowEntityPersistenceExceptionOnRepositoryError() {
    UUID id = UUID.randomUUID();

    doThrow(new RuntimeException("Unexpected error")).when(citizenRepository).deleteById(id);

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> citizenService.deleteCitizen(id));

    assertTrue(exception.getMessage().contains("Error while deleting citizen"));
    verify(citizenRepository).deleteById(id);
  }
}
