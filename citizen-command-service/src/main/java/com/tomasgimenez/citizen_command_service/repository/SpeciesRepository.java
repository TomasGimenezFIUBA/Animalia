package com.tomasgimenez.citizen_command_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;

@Repository
public interface SpeciesRepository extends JpaRepository<SpeciesEntity, UUID> {

}
