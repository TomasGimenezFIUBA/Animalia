package com.tomasgimenez.citizen_command_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.*;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.policy.role.RolePolicyValidator;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CitizenServiceImplTest {

  private CitizenRepository citizenRepository;
  private SpeciesService speciesService;
  private RolePolicyValidator rolePolicyValidator;
  private RoleService roleService;
  private CitizenServiceImpl citizenService;

  @BeforeEach
  void setUp() {
    citizenRepository = mock(CitizenRepository.class);
    speciesService = mock(SpeciesService.class);
    rolePolicyValidator = mock(RolePolicyValidator.class);
    roleService = mock(RoleService.class);

    citizenService = new CitizenServiceImpl(
        citizenRepository, speciesService, roleService);
    citizenService.setRolePolicyValidator(rolePolicyValidator);
  }

  @Test
  void createCitizen_shouldCreateSuccessfully() {
    UUID speciesId = UUID.randomUUID();
    Set<RoleName> roles = Set.of(RoleName.CIVIL);
    CreateCitizenRequest request = new CreateCitizenRequest("Luna", speciesId, true, roles);

    SpeciesEntity species = new SpeciesEntity();
    species.setId(speciesId);

    RoleEntity role = new RoleEntity();
    role.setName(RoleName.CIVIL);

    CitizenEntity savedEntity = CitizenEntity.builder()
        .id(UUID.randomUUID())
        .name("Luna")
        .hasHumanPet(true)
        .roles(Set.of(role))
        .species(species)
        .build();

    when(speciesService.getById(speciesId)).thenReturn(species);
    when(roleService.getRolesByRoleNames(roles)).thenReturn(Set.of(role));
    when(citizenRepository.save(any())).thenReturn(savedEntity);

    CitizenEntity result = citizenService.createCitizen(request);

    assertEquals("Luna", result.getName());
    verify(rolePolicyValidator).validate(roles, Optional.empty());
    verify(citizenRepository).save(any());
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

    SpeciesEntity spec1 = new SpeciesEntity(); spec1.setId(s1);
    SpeciesEntity spec2 = new SpeciesEntity(); spec2.setId(s2);

    RoleEntity civ = new RoleEntity(); civ.setName(RoleName.CIVIL);
    RoleEntity gen = new RoleEntity(); gen.setName(RoleName.GENERAL);

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
  }

  @Test
  void getCitizensByRoleName_shouldReturnCitizensSuccessfully() {
    RoleName roleName = RoleName.CIVIL;

    CitizenEntity citizen1 = new CitizenEntity();
    citizen1.setId(UUID.randomUUID());
    citizen1.setName("John Doe");

    CitizenEntity citizen2 = new CitizenEntity();
    citizen2.setId(UUID.randomUUID());
    citizen2.setName("Jane Doe");

    when(citizenRepository.findByRoleName(roleName)).thenReturn(List.of(citizen1, citizen2));

    Set<CitizenEntity> result = citizenService.getCitizensByRoleName(roleName);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("John Doe")));
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("Jane Doe")));
    verify(citizenRepository).findByRoleName(roleName);
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
  }

  @Test
  void updateCitizen_shouldUpdateFieldsSuccessfully() {
    UUID id = UUID.randomUUID();
    UpdateCitizenRequest request = new UpdateCitizenRequest(id, "Milo", null, true, null);

    CitizenEntity entity = new CitizenEntity();
    entity.setId(id);
    entity.setName("OldName");

    when(citizenRepository.findById(id)).thenReturn(Optional.of(entity));

    citizenService.updateCitizen(request);

    assertEquals("Milo", entity.getName());
    assertTrue(entity.isHasHumanPet());
    verify(citizenRepository).save(entity);
    verify(rolePolicyValidator, never()).validate(any(), any());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(speciesService, never()).getById(any());
  }

  @Test
  void updateCitizen_shouldUpdateHasHumanPetSuccessfully() {
    UUID id = UUID.randomUUID();
    UpdateCitizenRequest request = new UpdateCitizenRequest(id, null, null, true, null);

    CitizenEntity entity = new CitizenEntity();
    entity.setId(id);
    entity.setHasHumanPet(false);

    when(citizenRepository.findById(id)).thenReturn(Optional.of(entity));

    citizenService.updateCitizen(request);

    assertTrue(entity.isHasHumanPet());
    verify(citizenRepository).save(entity);
    verify(rolePolicyValidator, never()).validate(any(), any());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(speciesService, never()).getById(any());
  }

  @Test
  void updateCitizen_shouldUpdateSpeciesSuccessfully() {
    UUID id = UUID.randomUUID();
    UUID speciesId = UUID.randomUUID();
    UpdateCitizenRequest request = new UpdateCitizenRequest(id, null, speciesId, null, null);

    CitizenEntity entity = new CitizenEntity();
    entity.setId(id);

    SpeciesEntity species = new SpeciesEntity();
    species.setId(speciesId);

    when(citizenRepository.findById(id)).thenReturn(Optional.of(entity));
    when(speciesService.getById(speciesId)).thenReturn(species);

    citizenService.updateCitizen(request);

    assertEquals(species, entity.getSpecies());
    verify(citizenRepository).save(entity);
    verify(rolePolicyValidator, never()).validate(any(), any());
    verify(roleService, never()).getRolesByRoleNames(any());
    verify(speciesService).getById(speciesId);
  }

  @Test
  void updateCitizen_shouldUpdateRolesSuccessfully() {
    UUID id = UUID.randomUUID();
    Set<RoleName> roleNames = Set.of(RoleName.CIVIL);
    UpdateCitizenRequest request = new UpdateCitizenRequest(id, null, null, null, roleNames);

    CitizenEntity entity = new CitizenEntity();
    entity.setId(id);

    RoleEntity role = new RoleEntity();
    role.setName(RoleName.CIVIL);

    when(citizenRepository.findById(id)).thenReturn(Optional.of(entity));
    when(roleService.getRolesByRoleNames(roleNames)).thenReturn(Set.of(role));

    citizenService.updateCitizen(request);

    assertEquals(Set.of(role), entity.getRoles());
    verify(rolePolicyValidator).validate(roleNames, Optional.of(id));
    verify(citizenRepository).save(entity);
    verify(roleService).getRolesByRoleNames(roleNames);
    verify(speciesService, never()).getById(any());
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
}
