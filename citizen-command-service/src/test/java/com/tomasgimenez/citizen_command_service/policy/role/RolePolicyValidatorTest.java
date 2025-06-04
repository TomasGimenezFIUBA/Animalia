package com.tomasgimenez.citizen_command_service.policy.role;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolePolicyValidatorTest {

  private RolePolicy policy1;
  private RolePolicy policy2;
  private RolePolicyValidator validator;

  @BeforeEach
  void setUp() {
    policy1 = mock(RolePolicy.class);
    policy2 = mock(RolePolicy.class);
    validator = new RolePolicyValidator(List.of(policy1, policy2));
  }

  @Test
  void validate_shouldThrowExceptionWhenRolesIsEmpty() {
    Set<RoleName> emptyRoles = Set.of();
    Optional<UUID> excludeId = Optional.empty();

    var ex = assertThrows(RolePolicyException.class,
        () -> validator.validate(emptyRoles, excludeId));

    assertEquals("Roles cannot be empty.", ex.getMessage());
    verifyNoInteractions(policy1, policy2);
  }

  @Test
  void validate_shouldCallAllPolicies() {
    Set<RoleName> roles = Set.of(RoleName.CIVIL, RoleName.GENERAL);
    Optional<UUID> excludeId = Optional.of(UUID.randomUUID());

    validator.validate(roles, excludeId);

    verify(policy1).validate(roles, excludeId);
    verify(policy2).validate(roles, excludeId);
  }

  @Test
  void validateBulk_shouldThrowExceptionWhenListIsEmpty() {
    var ex = assertThrows(RolePolicyException.class,
        () -> validator.validateBulk(List.of()));

    assertEquals("Roles list cannot be empty.", ex.getMessage());
    verifyNoInteractions(policy1, policy2);
  }

  @Test
  void validateBulk_shouldThrowExceptionIfAnySetIsEmpty() {
    List<Set<RoleName>> rolesList = List.of(
        Set.of(RoleName.CIVIL),
        Set.of(),
        Set.of(RoleName.FIRST_MINISTER)
    );

    var ex = assertThrows(RolePolicyException.class,
        () -> validator.validateBulk(rolesList));

    assertEquals("Roles list cannot be empty.", ex.getMessage());
    verifyNoInteractions(policy1, policy2);
  }

  @Test
  void validateBulk_shouldCallAllPolicies() {
    List<Set<RoleName>> rolesList = List.of(
        Set.of(RoleName.CIVIL),
        Set.of(RoleName.FIRST_MINISTER)
    );

    validator.validateBulk(rolesList);

    verify(policy1).validateBulk(rolesList);
    verify(policy2).validateBulk(rolesList);
  }
}
