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
    Set<RoleEntity> result = roleNameSet.stream()
        .map(this::getRoleByName) // Use cached method for each role
        .collect(Collectors.toSet());

    if (result.size() != roleNameSet.size()) {
      log.warn("Not all roles found. Requested: {}, Found: {}", roleNameSet, result);
      throw new EntityNotFoundException(
          "Not all roles found for role names: " + roleNameSet.stream().toList());
    }

    return result;
  }

  @Cacheable(value = CacheConfig.ROLES_CACHE, key = "#roleName")
  @Override
  public RoleEntity getRoleByName(RoleName roleName) {
    return roleRepository.findByName(roleName).orElseThrow(
        () -> {
          log.warn("Role not found for name: {}", roleName);
          return new EntityNotFoundException("Role not found with name: " + roleName);
        }
      );
  }
}
