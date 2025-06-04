package com.tomasgimenez.citizen_command_service.jobs;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.model.entity.CitizenEventEntity;
import com.tomasgimenez.citizen_command_service.repository.OutboxCitizenEventRepository;
import com.tomasgimenez.citizen_command_service.service.CitizenEventProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitizenEventPublisherJob {
  private final OutboxCitizenEventRepository repository;
  private final CitizenEventProducerService citizenEventProducerService;

  @Scheduled(fixedDelayString = "${outbox-publisher.fixed-delay:5000}")
  public void publishPendingEvents() {
    List<CitizenEventEntity> events = repository.findOldestUnprocessedPerAggregateId(70);

    events.stream().map(event ->
        citizenEventProducerService.sendCitizenEvent(
            event.getAggregateId().toString(),
            event.getPayload(),
            event.getTopic(),
            result -> {
              event.setProcessed(true);
              repository.save(event);
              log.debug("Event published successfully: {}", event.getId());
            }
        )
    ).forEach(CompletableFuture::join);
  }
}
