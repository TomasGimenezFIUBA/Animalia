package com.tomasgimenez.citizen_command_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;

public interface CitizenEventRepository extends JpaRepository<CitizenEventEntity, UUID> {
  @Query(
      value = """
    SELECT *
    FROM outbox_citizen_events e
    WHERE e.processed = false
      AND e.created_at IN (
        SELECT MIN(created_at)
        FROM outbox_citizen_events
        WHERE processed = false
        GROUP BY aggregate_id
      )
    ORDER BY created_at ASC
    LIMIT :limit
    """,
      nativeQuery = true
  )
  List<CitizenEventEntity> findOldestUnprocessedPerAggregateId(@Param("limit") int limit);

  @Modifying
  @Query("UPDATE CitizenEventEntity c SET c.processed = true WHERE c.id IN :ids")
  void markAllAsProcessedById(@Param("ids") List<UUID> ids);
}
