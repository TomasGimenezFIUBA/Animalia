package com.tomasgimenez.citizen_command_service.model.entity;

import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citizens")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CitizenEntity {
  @Id @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;
  private String name;
  @Column(name = "has_human_pet")
  private boolean hasHumanPet;
  @ManyToOne
  private SpeciesEntity species;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "citizen_roles",
      joinColumns = @JoinColumn(name = "citizen_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<RoleEntity> roleEntities;

  @PrePersist
  private void prePersist() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }
}
