package com.tomasgimenez.citizen_command_service.service;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;

interface CitizenEventProducerService {
  void sendCitizenCreatedEvent(CitizenCreatedEvent event);
  void sendCitizenUpdatedEvent(CitizenUpdatedEvent event);
  void sendCitizenDeletedEvent(CitizenDeletedEvent event);
}