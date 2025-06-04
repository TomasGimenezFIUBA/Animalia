package com.tomasgimenez.citizen_command_service.model.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "species")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SpeciesEntity {

  @Id @GeneratedValue @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;
  private String name;
  @Column(name = "weight_kg")
  private Double weightKg;
  @Column(name = "height_mts")
  private Double heightMeters;
}

