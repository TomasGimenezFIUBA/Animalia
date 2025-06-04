package com.tomasgimenez.citizen_command_service.policy.role;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.exception.RolePolicyException;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

@Component
public class CivilRolePolicy implements RolePolicy {

  @Override
  public void validate(Set<RoleName> roles, Optional<UUID> excludeId) {
    if (roles.contains(RoleName.CIVIL) && roles.size() > 1) {
      throw new RolePolicyException("Civil role cannot be combined with other roles.");
    }
  }

  @Override
  public void validateBulk(List<Set<RoleName>> rolesList) {
    rolesList.forEach(roleNameSet -> validate(roleNameSet, Optional.empty()));
  }
}

