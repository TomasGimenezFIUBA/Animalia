package com.tomasgimenez.citizen_command_service.policy.role;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

public interface RolePolicy {
  void validate(Set<RoleName> roles, Optional<UUID> excludeId);
  void validateBulk(List<Set<RoleName>> rolesList);
}
