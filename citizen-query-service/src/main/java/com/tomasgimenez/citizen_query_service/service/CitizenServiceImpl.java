package com.tomasgimenez.citizen_query_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.tomasgimenez.citizen_query_service.exception.CitizenNotFoundException;
import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.repository.citizen.CitizenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitizenServiceImpl implements CitizenService{
  private final CitizenRepository citizenRepository;

  @Override
  public CitizenDocument insert(CitizenDocument citizenDocument) {
    try {
      return citizenRepository.insert(citizenDocument);
    } catch (DuplicateKeyException e) {
      log.warn("Duplicate key error while inserting citizen: {}", citizenDocument, e);
      return citizenDocument;
    }
  }

  @Override
  public void update(String id, CitizenPatch citizenPatch) {
    citizenRepository.update(id, citizenPatch);
  }

  @Override
  public void deleteById(String id) {
    citizenRepository.deleteById(id);
  }

  @Override
  public CitizenDocument findById(String id) {
    return citizenRepository.findById(id).orElseThrow(
        () -> new CitizenNotFoundException("Citizen with id " + id + " not found")
    );
  }

  @Override
  public Page<CitizenDocument> findByFilters(CitizenSearchParams params, Pageable pageable) {
    return citizenRepository.findByFilters(params, pageable);
  }
}
