package com.tomasgimenez.citizen_query_service.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.tomasgimenez.animalia.avro.*;
import com.tomasgimenez.citizen_query_service.exception.CorruptedCitizenDocumentException;
import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
import com.tomasgimenez.citizen_query_service.service.CitizenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CitizenEventHandlerTest {

  private CitizenService citizenService;
  private CitizenEventHandler handler;

  @BeforeEach
  void setup() {
    citizenService = mock(CitizenService.class);
    handler = new CitizenEventHandler(citizenService);
  }

  @Test
  void handleCreated_validEvent_callsInsertWithCorrectCitizen() {
    var species = new Species("species1", "Human", 70.0, 1.8);

    var event = new CitizenCreatedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        "id1",
        "Alice",
        true,
        species,
        List.of("admin", "user")
    );

    handler.handleCreated(event);

    ArgumentCaptor<CitizenDocument> captor = ArgumentCaptor.forClass(CitizenDocument.class);
    verify(citizenService).insert(captor.capture());
    CitizenDocument inserted = captor.getValue();

    assertEquals("id1", inserted.getId());
    assertEquals("Alice", inserted.getName());
    assertTrue(inserted.isHasHumanPet());
    assertEquals("Human", inserted.getSpecies().getName());
    assertEquals(List.of("admin", "user"), inserted.getRoleNames());
  }

  @Test
  void handleCreated_missingSpecies_throws() {
    var event = new CitizenCreatedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        "id1",
        "Alice",
        true,
        null,
        List.of("admin")
    );

    assertThrows(CorruptedCitizenDocumentException.class, () -> handler.handleCreated(event));
    verifyNoInteractions(citizenService);
  }

  @Test
  void handleCreated_emptyRoleNames_throws() {
    var species = new Species("species1", "Human", 70.0, 1.8);

    var event = new CitizenCreatedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        "id1",
        "Alice",
        true,
        species,
        List.of()
    );

    assertThrows(CorruptedCitizenDocumentException.class, () -> handler.handleCreated(event));
    verifyNoInteractions(citizenService);
  }

  @Test
  void handleUpdated_validEvent_callsUpdateWithCorrectPatch() {
    var species = new Species("species1", "Human", 80.0, 1.9);

    var event = new CitizenUpdatedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        "id2",
        "Bob",
        false,
        species,
        List.of("editor")
    );

    handler.handleUpdated(event);

    ArgumentCaptor<CitizenPatch> captor = ArgumentCaptor.forClass(CitizenPatch.class);
    verify(citizenService).update(eq("id2"), captor.capture());
    CitizenPatch patch = captor.getValue();

    assertTrue(patch.name().isPresent());
    assertEquals("Bob", patch.name().get());
    assertTrue(patch.hasHumanPet().isPresent());
    assertFalse(patch.hasHumanPet().get());
    assertTrue(patch.species().isPresent());
    assertEquals("Human", patch.species().get().getName());
    assertTrue(patch.roleNames().isPresent());
    assertEquals(List.of("editor"), patch.roleNames().get());
  }

  @Test
  void handleUpdated_missingId_throws() {
    var event = new CitizenUpdatedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        null,
        "Bob",
        false,
        null,
        null
    );

    assertThrows(CorruptedCitizenDocumentException.class, () -> handler.handleUpdated(event));
    verifyNoInteractions(citizenService);
  }

  @Test
  void handleDeleted_validEvent_callsDeleteById() {
    var event = new CitizenDeletedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        "id3"
    );

    handler.handleDeleted(event);

    verify(citizenService).deleteById("id3");
  }

  @Test
  void handleDeleted_missingId_throws() {
    var event = new CitizenDeletedEvent(
        "event123",
        "2024-01-01T10:00:00Z",
        "test-source",
        null
    );

    assertThrows(CorruptedCitizenDocumentException.class, () -> handler.handleDeleted(event));
    verifyNoInteractions(citizenService);
  }
}
