package com.tomasgimenez.citizen_command_service.policy.role;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.service.CitizenService;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
@Setter
public class UniqueRolePolicy implements RolePolicy {

  @Autowired
  @Lazy
  private CitizenService citizenService;

  private final List<RoleName> uniqueRoles = List.of(RoleName.FIRST_MINISTER, RoleName.TREASURER, RoleName.GENERAL);

  @Override
  public void validate(Set<RoleName> roles, Optional<UUID> excludeId) {
    if (roles.stream().noneMatch(uniqueRoles::contains)) {
      return; // No unique roles to validate
    }

    Set<RoleName> rolesToCheck = roles.stream()
        .filter(uniqueRoles::contains)
        .collect(Collectors.toSet());

    rolesToCheck.forEach(role -> {
      var roleTaken = citizenService.getCitizensByRoleName(role).stream()
          .anyMatch(citizen -> excludeId.isEmpty() || !excludeId.get().equals(citizen.getId()));
      if (roleTaken)
        throw new RolePolicyException("Role '" + role + "' is already assigned to another citizen.");
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

