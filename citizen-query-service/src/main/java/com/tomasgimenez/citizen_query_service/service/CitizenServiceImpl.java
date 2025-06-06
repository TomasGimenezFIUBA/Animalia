package com.tomasgimenez.citizen_query_service.service;

import java.util.function.Supplier;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;
import com.tomasgimenez.citizen_query_service.exception.CitizenNotFoundException;
import com.tomasgimenez.citizen_query_service.exception.DuplicatedCitizenException;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.repository.citizen.CitizenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitizenServiceImpl implements CitizenService {

  private final CitizenRepository citizenRepository;

  @Override
  public CitizenDocument insert(CitizenDocument citizenDocument) {
    return handleWriteRepository(() -> citizenRepository.insert(citizenDocument));
  }

  @Override
  public void update(String id, CitizenPatch citizenPatch) {
    handleWriteRepository(() -> {
      citizenRepository.update(id, citizenPatch);
      return null;
    });
  }

  @Override
  public void deleteById(String id) {
    handleWriteRepository(() -> {
      citizenRepository.deleteById(id);
      return null;
    });
  }

  @Override
  public CitizenDocument findById(String id) {
      return handleReadRepository(() -> citizenRepository.findById(id)).orElseThrow(
          () -> new CitizenNotFoundException("Citizen with id " + id + " not found"));
  }

  @Override
  public Page<CitizenDocument> findByFilters(CitizenSearchParams params, Pageable pageable) {
      return handleReadRepository(() -> citizenRepository.findByFilters(params, pageable));
  }

  private <T> T handleWriteRepository(Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (DuplicateKeyException e){
      log.warn("Duplicate key error: {}", e.getMessage(), e);
      throw new DuplicatedCitizenException(e.getMessage());
    } catch (DataAccessException e) {
      log.error("Database access error: {}", e.getMessage(), e);
      throw new DatabaseReadException("Error accessing database", e);
    } catch (Exception e) {
      log.error("Unexpected error: {}", e.getMessage(), e);
      throw new DatabaseWriteException("Unexpected error accessing database", e);
    }
  }

  private <T> T handleReadRepository (Supplier<T> supplier) {
    try{
      return supplier.get();
    }catch (Exception e) {
      log.error("Database access error: {}", e.getMessage(), e);
      throw new DatabaseReadException("Error accessing database", e);
    }
  }
}
