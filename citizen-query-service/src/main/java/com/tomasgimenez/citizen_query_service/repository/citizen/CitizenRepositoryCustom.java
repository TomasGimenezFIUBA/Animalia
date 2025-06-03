package com.tomasgimenez.citizen_query_service.repository.citizen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
public interface CitizenRepositoryCustom {
  Page<CitizenDocument> findByFilters(CitizenSearchParams params, Pageable pageable);
  void update(String id, CitizenPatch citizenPatch);
}
