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

    when(roleRepository.findRoleEntitiesByNameIn(List.of(role1, role2))).thenReturn(foundEntities);

    Set<RoleEntity> result = roleService.getRolesByRoleNames(requested);

    assertEquals(2, result.size());
    assertTrue(result.containsAll(foundEntities));
  }

  @Test
  void getRolesByRoleNames_shouldThrowWhenSomeRolesNotFound() {
    RoleName requestedRole = RoleName.GENERAL;

    when(roleRepository.findRoleEntitiesByNameIn(List.of(requestedRole))).thenReturn(List.of());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> roleService.getRolesByRoleNames(Set.of(requestedRole)));

    assertTrue(ex.getMessage().contains("Not all roles found"));
  }
}
