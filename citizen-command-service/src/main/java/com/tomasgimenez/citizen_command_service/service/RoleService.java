package com.tomasgimenez.citizen_command_service.service;

import java.util.Set;

import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

public interface RoleService {
  Set<RoleEntity> getRolesByRoleNames(Set<RoleName> roleNameSet);
  RoleEntity getRoleByName(RoleName roleName);
}
