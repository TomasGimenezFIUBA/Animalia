package com.tomasgimenez.citizen_command_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomasgimenez.citizen_command_service.model.entity.OutboxCitizenEventEntity;

public interface OutboxCitizenEventRepository extends JpaRepository<OutboxCitizenEventEntity, UUID> {
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
  List<OutboxCitizenEventEntity> findOldestUnprocessedPerAggregateId(@Param("limit") int limit);

}
