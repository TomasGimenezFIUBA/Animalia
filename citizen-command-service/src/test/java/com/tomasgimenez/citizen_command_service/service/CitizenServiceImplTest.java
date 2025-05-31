package com.tomasgimenez.citizen_command_service.service;

import com.tomasgimenez.citizen_command_service.model.dto.CitizenDTO;
import com.tomasgimenez.citizen_command_service.model.entity.*;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.policy.role.RolePolicyValidator;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        citizenRepository, speciesService, rolePolicyValidator, roleService);
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

    CitizenDTO result = citizenService.createCitizen(request);

    assertEquals("Luna", result.name());
    verify(rolePolicyValidator).validate(roles, Optional.empty());
    verify(citizenRepository).save(any());
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
  void deleteCitizen_shouldCallRepository() {
    UUID id = UUID.randomUUID();
    citizenService.deleteCitizen(id);
    verify(citizenRepository).deleteById(id);
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

    Set<CitizenDTO> result = citizenService.createCitizens(requests);

    assertEquals(2, result.size());
    verify(rolePolicyValidator).validateBulk(List.of(r1, r2));
    verify(citizenRepository).saveAll(any());
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
