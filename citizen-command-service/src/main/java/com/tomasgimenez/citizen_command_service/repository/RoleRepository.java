package com.tomasgimenez.citizen_command_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

  List<RoleEntity> findRoleEntitiesByNameIn(List<RoleName> roleNames);
  Optional<RoleEntity> findByName(RoleName roleName);
}
