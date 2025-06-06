package com.tomasgimenez.citizen_command_service.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_citizen_events")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CitizenEventEntity {
  @Id
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID aggregateId;

  @Column(nullable = false)
  private String type;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @JdbcTypeCode(SqlTypes.BINARY)
  private byte[] payload;
  private String topic;

  private boolean processed = false;

  private Instant createdAt = Instant.now();

  @PrePersist
  private void prePersist() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
