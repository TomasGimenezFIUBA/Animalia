package com.tomasgimenez.citizen_command_service.service;

import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
  void getRolesByRoleNames_shouldThrowWhenSomeRolesNotFound() {
    RoleName requestedRole = RoleName.GENERAL;

    when(roleRepository.findRoleEntitiesByNameIn(List.of(requestedRole))).thenReturn(List.of());

    assertThrows(EntityNotFoundException.class,
        () -> roleService.getRolesByRoleNames(Set.of(requestedRole)));
  }


  /*@Test
  void getRoleByName_shouldCacheResult() {
    RoleName roleName = RoleName.CIVIL;
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(roleName);

    when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));

    // First call - fetch from repository
    RoleEntity result1 = roleService.getRoleByName(roleName);
    assertEquals(roleEntity, result1);
    verify(roleRepository, times(1)).findByName(roleName);

    // Second call - fetch from cache
    RoleEntity result2 = roleService.getRoleByName(roleName);
    assertEquals(roleEntity, result2);
    verify(roleRepository, times(1)).findByName(roleName); // Repository should not be called again
  }*/

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
    RoleEntity roleEntity = RoleEntity.builder().name(roleName).build();
    when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));

    RoleEntity result = roleService.getRoleByName(roleName);

    assertEquals(roleEntity, result);
    verify(roleRepository, times(1)).findByName(roleName);
  }
}
