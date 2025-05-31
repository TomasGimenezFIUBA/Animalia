package com.tomasgimenez.citizen_command_service.service;

import com.tomasgimenez.citizen_command_service.model.entity.SpeciesEntity;
import com.tomasgimenez.citizen_command_service.repository.SpeciesRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpeciesServiceImplTest {

  private SpeciesRepository speciesRepository;
  private SpeciesServiceImpl speciesService;

  @BeforeEach
  void setUp() {
    speciesRepository = mock(SpeciesRepository.class);
    speciesService = new SpeciesServiceImpl(speciesRepository);
  }

  @Test
  void getById_shouldReturnSpeciesWhenFound() {
    UUID id = UUID.randomUUID();
    SpeciesEntity species = new SpeciesEntity();
    species.setId(id);

    when(speciesRepository.findById(id)).thenReturn(Optional.of(species));

    SpeciesEntity result = speciesService.getById(id);

    assertEquals(id, result.getId());
    verify(speciesRepository).findById(id);
  }

  @Test
  void getById_shouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(speciesRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> speciesService.getById(id));

    assertTrue(ex.getMessage().contains("Species not found"));
  }

  @Test
  void getByIds_shouldReturnSpeciesSetWhenAllFound() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    SpeciesEntity s1 = new SpeciesEntity();
    s1.setId(id1);
    SpeciesEntity s2 = new SpeciesEntity();
    s2.setId(id2);

    Set<UUID> ids = Set.of(id1, id2);
    List<SpeciesEntity> found = List.of(s1, s2);

    when(speciesRepository.findAllById(ids)).thenReturn(found);

    Set<SpeciesEntity> result = speciesService.getByIds(ids);

    assertEquals(2, result.size());
    assertTrue(result.containsAll(found));
    verify(speciesRepository).findAllById(ids);
  }

  @Test
  void getByIds_shouldThrowWhenSomeNotFound() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    SpeciesEntity s1 = new SpeciesEntity();
    s1.setId(id1);

    Set<UUID> ids = Set.of(id1, id2);
    List<SpeciesEntity> found = List.of(s1);

    when(speciesRepository.findAllById(ids)).thenReturn(found);

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> speciesService.getByIds(ids));

    assertTrue(ex.getMessage().contains("Not all species found"));
  }
}
