package com.tomasgimenez.citizen_command_service.policy.role;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.repository.CitizenRepository;

@Component
public class UniqueRolePolicy implements RolePolicy {

  private final CitizenRepository citizenRepository;

  private final List<RoleName> uniqueRoles = List.of(RoleName.FIRST_MINISTER, RoleName.TREASURER, RoleName.GENERAL);

  public UniqueRolePolicy(CitizenRepository citizenRepository) {
    this.citizenRepository = citizenRepository;
  }

  @Override
  public void validate(Set<RoleName> roles, Optional<UUID> excludeId) {
    if (roles.stream().noneMatch(uniqueRoles::contains)) {
      return; // No unique roles to validate
    }

    citizenRepository.findByRoleNamesIn(roles).stream()
      .filter(citizen -> excludeId.isEmpty() || !citizen.getId().equals(excludeId.get()))
      .forEach(citizen -> {
        var conflictingRoles = citizen.getRoles().stream()
          .map(RoleEntity::getName)
          .filter(uniqueRoles::contains)
          .toList();

        if (!conflictingRoles.isEmpty()) {
          throw new RolePolicyException("The following roles are not available: " + conflictingRoles);
        }
      });
  }

  @Override
  public void validateBulk(List<Set<RoleName>> rolesList) {
    if (rolesList.stream().noneMatch(roleSet -> roleSet.stream().anyMatch(uniqueRoles::contains))) {
      return; // No unique roles to validate
    }

    validateBulkInternal(rolesList);
    validateBulkExternal(rolesList);
  }

  private void validateBulkInternal(List<Set<RoleName>> rolesList) {
    Map<RoleName, Long> roleCounts = rolesList.stream()
        .flatMap(Set::stream)
        .filter(uniqueRoles::contains)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    roleCounts.forEach((role, count) -> {
      if (count > 1) {
        throw new RolePolicyException("Role '" + role + "' appears more than once in bulk insert.");
      }
    });
  }

  private void validateBulkExternal(List<Set<RoleName>> rolesList) {
    Set<RoleName> roleNameSet = rolesList.stream()
        .flatMap(Set::stream)
        .filter(uniqueRoles::contains)
      .collect(Collectors.toSet());

    validate(roleNameSet, Optional.empty());
  }
}

