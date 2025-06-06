package com.tomasgimenez.citizen_command_service.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_command_service.config.CacheConfig;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.repository.RoleRepository;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;

import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService{
  private final RoleRepository roleRepository;

  @Cacheable(value = CacheConfig.ROLES_CACHE, key = "#roleName")
  @Override
  public RoleEntity getRoleByName(RoleName roleName) {
    Optional<RoleEntity> optionalRole;
    try {
      optionalRole = roleRepository.findByName(roleName);
    } catch (Exception e) {
      log.error("Error fetching role by name: {}", roleName, e);
      throw new DatabaseReadException(
          "Error accessing database for role with name: " + roleName, e);
    }

    return optionalRole.orElseThrow(() -> {
      log.warn("Role not found for name: {}", roleName);
      return new EntityNotFoundException("Role not found with name: " + roleName);
    });
  }

  @Override
  public Set<RoleEntity> getRolesByRoleNames(Set<RoleName> roleNameSet) {
      return roleNameSet.stream()
          .map(this::getRoleByName) // Use cached method for each role
          .collect(Collectors.toSet());
  }
}
