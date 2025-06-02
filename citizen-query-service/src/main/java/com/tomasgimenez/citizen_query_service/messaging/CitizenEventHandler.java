package com.tomasgimenez.citizen_query_service.messaging;

import org.springframework.stereotype.Service;

import com.tomasgimenez.animalia.avro.CitizenCreatedEvent;
import com.tomasgimenez.animalia.avro.CitizenDeletedEvent;
import com.tomasgimenez.animalia.avro.CitizenUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CitizenEventHandler {

  //transactional??
  public void handleCreated(CitizenCreatedEvent citizenCreatedEvent){
    log.info("Received in " + CitizenCreatedEvent.class.getSimpleName() + ": " + citizenCreatedEvent);
    // Here you would typically save the event to a database or perform some other action
    // For example: citizenRepository.save(citizenCreatedEvent);
  }

  public void handleUpdated(CitizenUpdatedEvent citizenUpdatedEvent){
    log.info("Received in " + CitizenUpdatedEvent.class.getSimpleName() + ": " + citizenUpdatedEvent);
    // Here you would typically update the event in a database or perform some other action
    // For example: citizenRepository.update(citizenUpdatedEvent);
  }

  public void handleDeleted(CitizenDeletedEvent citizenDeletedEvent){
    log.info("Received in " + CitizenDeletedEvent.class.getSimpleName() + ": " + citizenDeletedEvent);
    // Here you would typically delete the event from a database or perform some other action
    // For example: citizenRepository.delete(citizenDeletedEvent);
  }
}
