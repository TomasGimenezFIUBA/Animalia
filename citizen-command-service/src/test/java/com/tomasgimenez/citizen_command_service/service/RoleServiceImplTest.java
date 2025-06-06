package com.tomasgimenez.citizen_command_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.repository.RoleRepository;

import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import com.tomasgimenez.citizen_common.exception.DatabaseAccessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleServiceImplTest {

  private RoleRepository roleRepository;
  private RoleServiceImpl roleService;

  @BeforeEach
  void setUp() {
    roleRepository = mock(RoleRepository.class);
    roleService = new RoleServiceImpl(roleRepository);
  }

  @Test
  void getRolesByRoleNames_shouldReturnRolesWhenAllFound() {
    RoleName role1 = RoleName.GENERAL;
    RoleName role2 = RoleName.CIVIL;

    RoleEntity entity1 = new RoleEntity();
    entity1.setName(role1);

    RoleEntity entity2 = new RoleEntity();
    entity2.setName(role2);

    List<RoleEntity> foundEntities = List.of(entity1, entity2);
    Set<RoleName> requested = Set.of(role1, role2);

    requested.forEach(roleName -> {
      RoleEntity entity = new RoleEntity();
      entity.setName(roleName);
      when(roleRepository.findByName(roleName)).thenReturn(Optional.of(entity));
    });

    Set<RoleEntity> result = roleService.getRolesByRoleNames(requested);

    assertEquals(2, result.size());
    assertTrue(result.containsAll(foundEntities));
  }

  @Test
  void getRoleByName_shouldThrowExceptionWhenRoleNotFound() {
    RoleName roleName = RoleName.GENERAL;

    when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> roleService.getRoleByName(roleName));

    assertEquals("Role not found with name: " + roleName, exception.getMessage());
    verify(roleRepository, times(1)).findByName(roleName);
  }

  @Test
  void getRoleByName_shouldReturnRoleWhenExists() {
    RoleName roleName = RoleName.GENERAL;
    RoleEntity roleEntity = new RoleEntity(UUID.randomUUID(), roleName);
    when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));

    RoleEntity result = roleService.getRoleByName(roleName);

    assertEquals(roleEntity, result);
    verify(roleRepository, times(1)).findByName(roleName);
  }

  @Test
  void getRoleByName_shouldThrowDatabaseAccessExceptionOnUnexpectedError() {
    RoleName roleName = RoleName.GENERAL;

    when(roleRepository.findByName(roleName)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseAccessException exception = assertThrows(DatabaseAccessException.class,
        () -> roleService.getRoleByName(roleName));

    assertTrue(exception.getMessage().contains("Error accessing database for role with name: " + roleName));
    verify(roleRepository, times(1)).findByName(roleName);
  }

  @Test
  void getRolesByRoleNames_shouldThrowDatabaseAccessExceptionOnUnexpectedError() {
    RoleName roleName = RoleName.GENERAL;
    Set<RoleName> roleNameSet = Set.of(roleName);

    when(roleRepository.findByName(roleName)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseAccessException exception = assertThrows(DatabaseAccessException.class,
        () -> roleService.getRolesByRoleNames(roleNameSet));

    assertTrue(exception.getMessage().contains("Error accessing database for role with name: " + roleName));
    verify(roleRepository, times(1)).findByName(roleName);
  }

  @Test
  void getRolesByRoleNames_shouldThrowEntityNotFoundExceptionWhenRoleNotFound() {
    RoleName roleName = RoleName.GENERAL;
    Set<RoleName> roleNameSet = Set.of(roleName);

    when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> roleService.getRolesByRoleNames(roleNameSet));

    assertTrue(exception.getMessage().contains("Role not found with name: " + roleName));
    verify(roleRepository, times(1)).findByName(roleName);
  }
}
