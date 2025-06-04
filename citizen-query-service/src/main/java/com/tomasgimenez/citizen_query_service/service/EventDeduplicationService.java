package com.tomasgimenez.citizen_query_service.service;

public interface EventDeduplicationService {
    boolean isEventProcessed(String eventId);

    void markEventAsProcessed(String eventId);
}
