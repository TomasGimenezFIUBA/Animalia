package com.tomasgimenez.citizen_command_service.policy.role;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;
import com.tomasgimenez.citizen_command_service.service.CitizenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniqueRolePolicyTest {

  private CitizenService citizenService;
  private UniqueRolePolicy uniqueRolePolicy;

  @BeforeEach
  void setUp() {
    citizenService = mock(CitizenService.class);
    uniqueRolePolicy = new UniqueRolePolicy();
    uniqueRolePolicy.setCitizenService(citizenService);
  }

  private CitizenEntity createCitizenWithRoles(Set<RoleName> roles) {
    Set<RoleEntity> roleEntities = new HashSet<>();
    for (RoleName roleName : roles) {
      RoleEntity roleEntity = new RoleEntity();
      roleEntity.setName(roleName);
      roleEntities.add(roleEntity);
    }

    return CitizenEntity.builder()
        .id(UUID.randomUUID())
        .roles(roleEntities)
        .species(new SpeciesEntity())
        .build();
  }

  @Test
  void validate_shouldDoNothingWhenNoUniqueRoles() {
    Set<RoleName> roles = Set.of(RoleName.CIVIL, RoleName.MINISTER_OF_STATE);

    uniqueRolePolicy.validate(roles, Optional.empty());

    verifyNoInteractions(citizenService);
  }

  @Test
  void validateBulk_shouldDoNothingIfNoUniqueRolesPresent() {
    List<Set<RoleName>> bulk = List.of(
        Set.of(RoleName.CIVIL),
        Set.of(RoleName.MINISTER_OF_STATE)
    );

    assertDoesNotThrow(() -> uniqueRolePolicy.validateBulk(bulk));
    verifyNoInteractions(citizenService);
  }

  @Test
  void validateBulk_shouldThrowWhenInternalDuplicationExists() {
    List<Set<RoleName>> bulk = List.of(
        Set.of(RoleName.GENERAL),
        Set.of(RoleName.GENERAL)
    );

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> uniqueRolePolicy.validateBulk(bulk));

    assertTrue(ex.getMessage().contains("GENERAL"));
  }

  @Test
  void validate_shouldPassWhenNoConflictFound() {
    Set<RoleName> roles = Set.of(RoleName.FIRST_MINISTER);

    when(citizenService.getCitizensByRoleName(RoleName.FIRST_MINISTER))
        .thenReturn(Collections.emptySet());

    assertDoesNotThrow(() -> uniqueRolePolicy.validate(roles, Optional.empty()));
  }

  @Test
  void validate_shouldThrowWhenConflictFound() {
    Set<RoleName> roles = Set.of(RoleName.GENERAL);
    RoleName role = RoleName.GENERAL;

    when(citizenService.getCitizensByRoleName(role))
        .thenReturn(Set.of(createCitizenWithRoles(Set.of(role))));

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> uniqueRolePolicy.validate(roles, Optional.empty()));

    assertTrue(ex.getMessage().contains("GENERAL"));
  }

  @Test
  void validate_shouldIgnoreExcludedCitizenId() {
    Set<RoleName> roles = Set.of(RoleName.TREASURER);
    RoleName role = RoleName.TREASURER;
    var citizen = createCitizenWithRoles(Set.of(role));

    when(citizenService.getCitizensByRoleName(role))
        .thenReturn(Set.of(citizen));

    assertDoesNotThrow(() -> uniqueRolePolicy.validate(roles, Optional.of(citizen.getId())));
  }

  @Test
  void validateBulk_shouldThrowWhenExternalConflictExists() {
    RoleName conflictRole = RoleName.FIRST_MINISTER;

    when(citizenService.getCitizensByRoleName(conflictRole))
        .thenReturn(Set.of(createCitizenWithRoles(Set.of(conflictRole))));

    List<Set<RoleName>> bulk = List.of(
        Set.of(conflictRole),
        Set.of(RoleName.CIVIL)
    );

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> uniqueRolePolicy.validateBulk(bulk));

    assertTrue(ex.getMessage().contains("FIRST_MINISTER"));
  }

  @Test
  void validateBulk_shouldPassWhenNoConflicts() {
    when(citizenService.getCitizensByRoleName(any()))
        .thenReturn(Set.of());

    List<Set<RoleName>> bulk = List.of(
        Set.of(RoleName.FIRST_MINISTER),
        Set.of(RoleName.TREASURER),
        Set.of(RoleName.GENERAL)
    );

    assertDoesNotThrow(() -> uniqueRolePolicy.validateBulk(bulk));
  }
}
