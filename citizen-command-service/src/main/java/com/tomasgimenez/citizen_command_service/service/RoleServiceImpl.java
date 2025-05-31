package com.tomasgimenez.citizen_command_service.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService{
  private final RoleRepository roleRepository;

  @Override
  public Set<RoleEntity> getRolesByRoleNames(Set<RoleName> roleNameSet) {
    var result = roleRepository.findRoleEntitiesByNameIn(roleNameSet.stream().toList());
    if (result.size() != roleNameSet.size()) {
      log.warn("Not all roles found. Requested: {}, Found: {}", roleNameSet, result);
      throw new EntityNotFoundException(
          "Not all roles found for role names: " + roleNameSet.stream().toList());
    }
    return Set.copyOf(result);
  }
}
