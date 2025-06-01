package com.tomasgimenez.citizen_command_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

  Optional<RoleEntity> findByName(RoleName roleName);
}
