package com.tomasgimenez.citizen_command_service.policy.role;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniqueRolePolicyTest {

  private CitizenRepository citizenRepository;
  private UniqueRolePolicy uniqueRolePolicy;

  @BeforeEach
  void setUp() {
    citizenRepository = mock(CitizenRepository.class);
    uniqueRolePolicy = new UniqueRolePolicy(citizenRepository);
  }

  @Test
  void validate_shouldDoNothingWhenNoUniqueRoles() {
    Set<RoleName> roles = Set.of(RoleName.CIVIL, RoleName.MINISTER_OF_STATE);

    uniqueRolePolicy.validate(roles, Optional.empty());

    verifyNoInteractions(citizenRepository);
  }

  @Test
  void validate_shouldPassWhenNoConflictFound() {
    Set<RoleName> roles = Set.of(RoleName.FIRST_MINISTER);

    when(citizenRepository.findByRoleNamesIn(roles)).thenReturn(List.of());

    assertDoesNotThrow(() -> uniqueRolePolicy.validate(roles, Optional.empty()));
  }

  @Test
  void validate_shouldThrowWhenConflictFound() {
    Set<RoleName> roles = Set.of(RoleName.GENERAL);

    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(RoleName.GENERAL);

    CitizenEntity existing = new CitizenEntity();
    existing.setId(UUID.randomUUID());
    existing.setRoles(Set.of(roleEntity));

    when(citizenRepository.findByRoleNamesIn(roles)).thenReturn(List.of(existing));

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> uniqueRolePolicy.validate(roles, Optional.empty()));

    assertTrue(ex.getMessage().contains("GENERAL"));
  }

  @Test
  void validate_shouldIgnoreExcludedCitizenId() {
    UUID myId = UUID.randomUUID();
    Set<RoleName> roles = Set.of(RoleName.TREASURER);

    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(RoleName.TREASURER);

    CitizenEntity me = new CitizenEntity();
    me.setId(myId);
    me.setRoles(Set.of(roleEntity));

    when(citizenRepository.findByRoleNamesIn(roles)).thenReturn(List.of(me));

    assertDoesNotThrow(() -> uniqueRolePolicy.validate(roles, Optional.of(myId)));
  }

  @Test
  void validateBulk_shouldDoNothingIfNoUniqueRolesPresent() {
    List<Set<RoleName>> bulk = List.of(
        Set.of(RoleName.CIVIL),
        Set.of(RoleName.MINISTER_OF_STATE)
    );

    assertDoesNotThrow(() -> uniqueRolePolicy.validateBulk(bulk));
    verifyNoInteractions(citizenRepository);
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
  void validateBulk_shouldThrowWhenExternalConflictExists() {
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(RoleName.FIRST_MINISTER);

    CitizenEntity existing = new CitizenEntity();
    existing.setId(UUID.randomUUID());
    existing.setRoles(Set.of(roleEntity));

    when(citizenRepository.findByRoleNamesIn(any())).thenReturn(List.of(existing));

    List<Set<RoleName>> bulk = List.of(
        Set.of(RoleName.FIRST_MINISTER),
        Set.of(RoleName.CIVIL)
    );

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> uniqueRolePolicy.validateBulk(bulk));

    assertTrue(ex.getMessage().contains("FIRST_MINISTER"));
  }

  @Test
  void validateBulk_shouldPassWhenNoConflicts() {
    when(citizenRepository.findByRoleNamesIn(any())).thenReturn(List.of());

    List<Set<RoleName>> bulk = List.of(
        Set.of(RoleName.FIRST_MINISTER),
        Set.of(RoleName.TREASURER),
        Set.of(RoleName.GENERAL)
    );

    assertDoesNotThrow(() -> uniqueRolePolicy.validateBulk(bulk));
  }
}
