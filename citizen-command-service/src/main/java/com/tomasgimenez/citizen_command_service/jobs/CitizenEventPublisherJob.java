package com.tomasgimenez.citizen_command_service.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.service.CitizenEventProducerService;
import com.tomasgimenez.citizen_command_service.service.CitizenEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitizenEventPublisherJob {
  private final CitizenEventService citizenEventService;
  private final CitizenEventProducerService citizenEventProducerService;

  @Scheduled(fixedDelayString = "${outbox-publisher.fixed-delay:5000}")
  public void publishPendingEvents() {
    List<CitizenEventEntity> events = citizenEventService.getOldestUnprocessedPerAggregateId(70);

    if (events.isEmpty()) {
      log.debug("No pending events to publish");
      return;
    }

    ConcurrentLinkedQueue<UUID> processedIds = new ConcurrentLinkedQueue<>();

    events.stream()
        .map(event -> {
          try {
            return handleSendCitizenEvent(event, processedIds);
          }catch (Exception e) {
            log.error("Error sending event {}: {}", event.getId(), e.getMessage(), e);
            return CompletableFuture.completedFuture(null);
          }
        })
        .toList()
        .forEach(CompletableFuture::join);

    markAsProcessed(processedIds);
  }

  private CompletableFuture<SendResult<String, byte[]>> handleSendCitizenEvent(CitizenEventEntity event,
      ConcurrentLinkedQueue<UUID> processedIds) {
    return citizenEventProducerService.sendCitizenEvent(
        event.getAggregateId().toString(),
        event.getPayload(),
        event.getTopic(),
        result -> {
          event.setProcessed(true);
          processedIds.add(event.getId());
          log.debug("Event published successfully: {}", event.getId());
        }).exceptionally(ex -> {
      log.error("Failed to publish event {}: {}", event.getId(), ex.getMessage(), ex);
      return null;
    });
  }

  private void markAsProcessed(ConcurrentLinkedQueue<UUID> processedIds) {
    if (processedIds.isEmpty()) {
      log.debug("No events were successfully processed");
      return;
    }

    try {
      citizenEventService.markAllAsProcessedById(new ArrayList<>(processedIds));
      log.debug("Marked {} events as processed", processedIds.size());
    } catch (Exception e) {
      log.error("Failed to mark events as processed: {}", e.getMessage(), e);
    }
  }
}