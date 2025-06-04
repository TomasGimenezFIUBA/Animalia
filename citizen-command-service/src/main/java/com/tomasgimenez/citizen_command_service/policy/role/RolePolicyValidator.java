package com.tomasgimenez.citizen_command_service.policy.role;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RolePolicyValidator {
  private List<RolePolicy> rolePolicies;

  public void validate(Set<RoleName> roles, Optional<UUID> excludeCitizenId) {
    if (roles.isEmpty()) {
      throw new RolePolicyException("Roles cannot be empty.");
    }

    rolePolicies.forEach(policy -> policy.validate(roles, excludeCitizenId));
  }

  public void validateBulk(List<Set<RoleName>> rolesList){
    if (rolesList.isEmpty() || rolesList.stream().anyMatch(Set::isEmpty)) {
      throw new RolePolicyException("Roles list cannot be empty.");
    }

    rolePolicies.forEach(policy -> policy.validateBulk(rolesList));
  }
}
