package com.tomasgimenez.citizen_command_service.policy.role;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CivilRolePolicyTest {

  private CivilRolePolicy civilRolePolicy;

  @BeforeEach
  void setUp() {
    civilRolePolicy = new CivilRolePolicy();
  }

  @Test
  void validate_shouldAllowOnlyCivil() {
    Set<RoleName> roles = Set.of(RoleName.CIVIL);
    assertDoesNotThrow(() -> civilRolePolicy.validate(roles, Optional.empty()));
  }

  @Test
  void validate_shouldAllowNonCivilCombination() {
    Set<RoleName> roles = Set.of(RoleName.MINISTER_OF_STATE, RoleName.GENERAL);
    assertDoesNotThrow(() -> civilRolePolicy.validate(roles, Optional.empty()));
  }

  @Test
  void validate_shouldThrowWhenCivilIsCombinedWithOtherRole() {
    Set<RoleName> roles = Set.of(RoleName.CIVIL, RoleName.FIRST_MINISTER);

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> civilRolePolicy.validate(roles, Optional.empty()));

    assertEquals("Civil role cannot be combined with other roles.", ex.getMessage());
  }

  @Test
  void validateBulk_shouldAllowValidList() {
    List<Set<RoleName>> rolesList = List.of(
        Set.of(RoleName.CIVIL),
        Set.of(RoleName.MINISTER_OF_STATE),
        Set.of(RoleName.GENERAL)
    );

    assertDoesNotThrow(() -> civilRolePolicy.validateBulk(rolesList));
  }

  @Test
  void validateBulk_shouldThrowIfAnySetIsInvalid() {
    List<Set<RoleName>> rolesList = List.of(
        Set.of(RoleName.CIVIL),
        Set.of(RoleName.CIVIL, RoleName.SECRETARY_OF_STATE)
    );

    RolePolicyException ex = assertThrows(RolePolicyException.class,
        () -> civilRolePolicy.validateBulk(rolesList));

    assertEquals("Civil role cannot be combined with other roles.", ex.getMessage());
  }
}

