package com.tomasgimenez.citizen_command_service.jobs;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.repository.OutboxCitizenEventRepository;
import com.tomasgimenez.citizen_command_service.service.CitizenEventProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

class CitizenEventPublisherJobTest {

  private OutboxCitizenEventRepository repository;
  private CitizenEventProducerService producerService;
  private CitizenEventPublisherJob job;

  @BeforeEach
  void setUp() {
    repository = mock(OutboxCitizenEventRepository.class);
    producerService = mock(CitizenEventProducerService.class);
    job = new CitizenEventPublisherJob(repository, producerService);
  }

  @Test
  void publishPendingEvents_shouldSendAndMarkEventsAsProcessed() {
    UUID aggregateId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    byte[] payload = "data".getBytes();
    CitizenEventEntity event = CitizenEventEntity.builder()
        .id(id)
        .aggregateId(aggregateId)
        .aggregateType("Citizen")
        .type("CREATED")
        .payload(payload)
        .topic("citizen-topic")
        .processed(false)
        .build();

    when(repository.findOldestUnprocessedPerAggregateId(70)).thenReturn(List.of(event));

    doAnswer(invocation -> {
      Consumer<Void> callback = invocation.getArgument(3);
      callback.accept(null);
      return CompletableFuture.completedFuture(null);
    }).when(producerService).sendCitizenEvent(
        eq(aggregateId.toString()),
        eq(payload),
        eq("citizen-topic"),
        any()
    );

    job.publishPendingEvents();

    verify(producerService).sendCitizenEvent(eq(aggregateId.toString()), eq(payload), eq("citizen-topic"), any());
    verify(repository).save(argThat(saved -> saved.isProcessed() && saved.getId().equals(id)));
  }

  @Test
  void publishPendingEvents_shouldDoNothing_whenNoEventsAreFound() {
    when(repository.findOldestUnprocessedPerAggregateId(70)).thenReturn(List.of());

    job.publishPendingEvents();

    verifyNoInteractions(producerService);
    verify(repository, never()).save(any());
  }
}
