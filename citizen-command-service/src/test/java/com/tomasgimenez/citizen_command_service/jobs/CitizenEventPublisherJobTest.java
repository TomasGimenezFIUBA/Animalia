package com.tomasgimenez.citizen_command_service.jobs;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.messaging.CitizenEventProducer;
import com.tomasgimenez.citizen_command_service.service.CitizenEventService;
import com.tomasgimenez.citizen_common.exception.MessageProductionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.support.SendResult;

class CitizenEventPublisherJobTest {

  @Mock
  private CitizenEventService citizenEventService;

  @Mock
  private CitizenEventProducer citizenEventProducer;

  @InjectMocks
  private CitizenEventPublisherJob job;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldDoNothingWhenNoEvents() {
    when(citizenEventService.getOldestUnprocessedPerAggregateId(70)).thenReturn(Collections.emptyList());

    job.publishPendingEvents();

    verify(citizenEventService, never()).markAllAsProcessedById(anyList());
    verifyNoInteractions(citizenEventProducer);
  }

  @Test
  void shouldPublishAndMarkEventsSuccessfully() throws MessageProductionException {
    UUID id = UUID.randomUUID();
    CitizenEventEntity event = buildEvent(id);
    when(citizenEventService.getOldestUnprocessedPerAggregateId(70)).thenReturn(List.of(event));
    when(citizenEventProducer.sendCitizenEvent(any(), any(), any(), any()))
        .thenAnswer(invocation -> {
          var callback = invocation.getArgument(3, java.util.function.Consumer.class);
          callback.accept(null); // simulate success
          return CompletableFuture.completedFuture(null);
        });

    job.publishPendingEvents();

    assertTrue(event.isProcessed());
    verify(citizenEventService).markAllAsProcessedById(List.of(id));
  }

  @Test
  void shouldIgnoreEventIfSendingFails() throws MessageProductionException {
    UUID id = UUID.randomUUID();
    CitizenEventEntity event = buildEvent(id);
    when(citizenEventService.getOldestUnprocessedPerAggregateId(70)).thenReturn(List.of(event));
    when(citizenEventProducer.sendCitizenEvent(any(), any(), any(), any()))
        .thenThrow(new MessageProductionException("fail", new RuntimeException()));

    job.publishPendingEvents();

    assertFalse(event.isProcessed());
    verify(citizenEventService, never()).markAllAsProcessedById(any());
  }

  @Test
  void shouldLogErrorWhenMarkingProcessedFails() throws MessageProductionException {
    UUID id = UUID.randomUUID();
    CitizenEventEntity event = buildEvent(id);
    when(citizenEventService.getOldestUnprocessedPerAggregateId(70)).thenReturn(List.of(event));
    when(citizenEventProducer.sendCitizenEvent(any(), any(), any(), any()))
        .thenAnswer(invocation -> {
          var callback = invocation.getArgument(3, java.util.function.Consumer.class);
          callback.accept(null);
          return CompletableFuture.completedFuture(null);
        });

    doThrow(new RuntimeException("DB failure"))
        .when(citizenEventService).markAllAsProcessedById(List.of(id));

    job.publishPendingEvents();

    verify(citizenEventService).markAllAsProcessedById(List.of(id));
    assertTrue(event.isProcessed());
  }

  @Test
  void shouldProcessOnlySuccessfulEventsWhenOneFails() throws MessageProductionException {
    CitizenEventEntity event1 = buildEvent(UUID.randomUUID());
    CitizenEventEntity event2 = buildEvent(UUID.randomUUID());
    CitizenEventEntity event3 = buildEvent(UUID.randomUUID());

    List<CitizenEventEntity> events = List.of(event1, event2, event3);
    when(citizenEventService.getOldestUnprocessedPerAggregateId(70)).thenReturn(events);

    when(citizenEventProducer.sendCitizenEvent(any(), any(), any(), any()))
        .thenAnswer(invocation -> {
          String aggregateId = invocation.getArgument(0);
          var callback = invocation.getArgument(3, java.util.function.Consumer.class);

          if (aggregateId.equals(event2.getAggregateId().toString())) {
            CompletableFuture<SendResult<String, byte[]>> failed = new CompletableFuture<>();
            failed.completeExceptionally(new RuntimeException("Simulated failure"));
            return failed;
          }

          callback.accept(null);
          return CompletableFuture.completedFuture(null);
        });

    job.publishPendingEvents();

    assertTrue(event1.isProcessed());
    assertFalse(event2.isProcessed());
    assertTrue(event3.isProcessed());

    ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);
    verify(citizenEventService).markAllAsProcessedById(captor.capture());

    List<UUID> processedIds = captor.getValue();
    assertEquals(2, processedIds.size());
    assertTrue(processedIds.contains(event1.getId()));
    assertTrue(processedIds.contains(event3.getId()));
    assertFalse(processedIds.contains(event2.getId()));
  }


  private CitizenEventEntity buildEvent(UUID id) {
    CitizenEventEntity event = new CitizenEventEntity();
    event.setId(id);
    event.setAggregateId(UUID.randomUUID());
    event.setPayload("payload".getBytes());
    event.setTopic("topic");
    event.setProcessed(false);
    return event;
  }
}
