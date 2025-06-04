package com.tomasgimenez.citizen_command_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

@Repository
public interface CitizenRepository extends JpaRepository<CitizenEntity, UUID> {
  @Query("""
    SELECT c FROM CitizenEntity c
    JOIN c.roleEntities r
    WHERE r.name = :roleName
""")
  List<CitizenEntity> findByRoleName(@Param("roleName") RoleName roleName);

  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({
      @QueryHint(name = "javax.persistence.lock.timeout", value = "3000")
  })
  @Query("SELECT c FROM CitizenEntity c WHERE c.id = :id")
  Optional<CitizenEntity> findByIdForUpdate(@Param("id") UUID id);
}
