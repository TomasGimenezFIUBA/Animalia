package com.tomasgimenez.citizen_query_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;

public interface CitizenService {
  CitizenDocument insert(CitizenDocument citizenDocument);
  void update(String id, CitizenPatch citizenPatch);
  void deleteById(String id);
  CitizenDocument findById(String id);
  Page<CitizenDocument> findByFilters(CitizenSearchParams params, Pageable pageable);

}
