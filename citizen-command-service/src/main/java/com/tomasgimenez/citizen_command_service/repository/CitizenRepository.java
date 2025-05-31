package com.tomasgimenez.citizen_command_service.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

@Repository
public interface CitizenRepository extends JpaRepository<CitizenEntity, UUID> {
  @Query("""
    SELECT c FROM CitizenEntity c
    JOIN c.roles r
    WHERE r.name IN :roleNames
""")
  List<CitizenEntity> findByRoleNamesIn(@Param("roleNames") Set<RoleName> roleNames);
}
