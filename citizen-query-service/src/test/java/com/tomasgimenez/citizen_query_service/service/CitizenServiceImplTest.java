package com.tomasgimenez.citizen_query_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.mongodb.DuplicateKeyException;
import com.tomasgimenez.citizen_query_service.exception.CitizenNotFoundException;
import com.tomasgimenez.citizen_query_service.model.*;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.repository.citizen.CitizenRepository;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

class CitizenServiceImplTest {

  @Mock
  private CitizenRepository repository;

  private CitizenServiceImpl service;

  private CitizenDocument sampleCitizen;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new CitizenServiceImpl(repository);
    sampleCitizen = CitizenDocument.builder()
        .id("123")
        .name("Alice")
        .build();
  }

  @Test
  void insert_returnsDocument_whenSuccessful() {
    when(repository.insert(sampleCitizen)).thenReturn(sampleCitizen);
    CitizenDocument result = service.insert(sampleCitizen);
    assertEquals(sampleCitizen, result);
  }

  @Test
  void insert_returnsOriginal_whenDuplicateKeyException() {
    when(repository.insert(sampleCitizen)).thenThrow(DuplicateKeyException.class);
    CitizenDocument result = service.insert(sampleCitizen);
    assertEquals(sampleCitizen, result);
  }

  @Test
  void update_delegatesToRepository() {
    CitizenPatch patch = mock(CitizenPatch.class);
    service.update("123", patch);
    verify(repository).update("123", patch);
  }

  @Test
  void deleteById_delegatesToRepository() {
    service.deleteById("123");
    verify(repository).deleteById("123");
  }

  @Test
  void findById_returnsCitizen_whenExists() {
    when(repository.findById("123")).thenReturn(Optional.of(sampleCitizen));
    CitizenDocument result = service.findById("123");
    assertEquals(sampleCitizen, result);
  }

  @Test
  void findById_throwsException_whenNotFound() {
    when(repository.findById("123")).thenReturn(Optional.empty());
    assertThrows(CitizenNotFoundException.class, () -> service.findById("123"));
  }

  @Test
  void findByFilters_returnsPageFromRepository() {
    Pageable pageable = PageRequest.of(0, 10);
    CitizenSearchParams params = new CitizenSearchParams("Alice", null, null, null, null, null);
    Page<CitizenDocument> expectedPage = new PageImpl<>(List.of(sampleCitizen));
    when(repository.findByFilters(params, pageable)).thenReturn(expectedPage);
    Page<CitizenDocument> result = service.findByFilters(params, pageable);
    assertEquals(expectedPage, result);
  }
}
