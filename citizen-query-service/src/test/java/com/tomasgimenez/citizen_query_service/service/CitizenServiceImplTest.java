package com.tomasgimenez.citizen_query_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.mongodb.DuplicateKeyException;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;
import com.tomasgimenez.citizen_query_service.exception.CitizenNotFoundException;
import com.tomasgimenez.citizen_query_service.exception.DuplicatedCitizenException;
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
  void insert_throwsDuplicatedCitizenException_whenDuplicateKeyException() {
    when(repository.insert(sampleCitizen)).thenThrow(DuplicateKeyException.class);

    assertThrows(DuplicatedCitizenException.class, () -> service.insert(sampleCitizen));
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

  @Test
  void insert_shouldThrowDatabaseWriteException_whenUnexpectedExceptionIsThrown() {
    when(repository.insert(sampleCitizen)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> service.insert(sampleCitizen));

    assertEquals("Unexpected error accessing database", exception.getMessage());
  }

  @Test
  void update_shouldThrowDatabaseWriteException_whenUnexpectedExceptionIsThrown() {
    doThrow(new RuntimeException("Unexpected error")).when(repository).update(eq("123"), any(CitizenPatch.class));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> service.update("123", mock(CitizenPatch.class)));

    assertEquals("Unexpected error accessing database", exception.getMessage());
  }

  @Test
  void deleteById_shouldThrowDatabaseWriteException_whenUnexpectedExceptionIsThrown() {
    doThrow(new RuntimeException("Unexpected error")).when(repository).deleteById("123");

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> service.deleteById("123"));

    assertEquals("Unexpected error accessing database", exception.getMessage());
  }

  @Test
  void findById_shouldThrowDatabaseReadException_whenUnexpectedExceptionIsThrown() {
    when(repository.findById("123")).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseReadException exception = assertThrows(DatabaseReadException.class,
        () -> service.findById("123"));

    assertEquals("Error accessing database", exception.getMessage());
  }

  @Test
  void findByFilters_shouldThrowDatabaseReadException_whenUnexpectedExceptionIsThrown() {
    Pageable pageable = PageRequest.of(0, 10);
    CitizenSearchParams params = new CitizenSearchParams("Alice", null, null, null, null, null);
    when(repository.findByFilters(params, pageable)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseReadException exception = assertThrows(DatabaseReadException.class,
        () -> service.findByFilters(params, pageable));

    assertEquals("Error accessing database", exception.getMessage());
  }
}
