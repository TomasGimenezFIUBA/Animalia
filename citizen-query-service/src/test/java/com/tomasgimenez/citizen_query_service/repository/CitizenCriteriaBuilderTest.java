package com.tomasgimenez.citizen_query_service.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.repository.citizen.CitizenCriteriaBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;

class CitizenCriteriaBuilderTest {

  @Test
  void build_returnsEmptyCriteria_whenAllParamsNull() {
    CitizenSearchParams params = new CitizenSearchParams();
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    assertTrue(criteria.getCriteriaObject().isEmpty());
  }

  @Test
  void build_includesNameRegexFilter() {
    CitizenSearchParams params = new CitizenSearchParams();
    params.setName("alice");
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    assertTrue(criteria.getCriteriaObject().toJson().contains("name"));
    assertTrue(criteria.getCriteriaObject().toJson().contains("alice"));
  }

  @Test
  void build_includesSpeciesNameRegexFilter() {
    CitizenSearchParams params = new CitizenSearchParams();
    params.setSpeciesName("human");
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    assertTrue(criteria.getCriteriaObject().toJson().contains("species.name"));
    assertTrue(criteria.getCriteriaObject().toJson().contains("human"));
  }

  @Test
  void build_includesNumericFilters() {
    CitizenSearchParams params = new CitizenSearchParams();
    params.setSpeciesWeight(70.5);
    params.setSpeciesHeight(1.75);
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    String json = criteria.getCriteriaObject().toJson();
    assertTrue(json.contains("species.weight"));
    assertTrue(json.contains("species.height"));
  }

  @Test
  void build_includesRoleNamesInFilter() {
    CitizenSearchParams params = new CitizenSearchParams();
    params.setRoleNames(List.of("admin", "user"));
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    assertTrue(criteria.getCriteriaObject().toJson().contains("roleNames"));
  }

  @Test
  void build_includesHasHumanPetFilter() {
    CitizenSearchParams params = new CitizenSearchParams();
    params.setHasHumanPet(true);
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    assertTrue(criteria.getCriteriaObject().toJson().contains("hasHumanPet"));
  }

  @Test
  void build_combinesMultipleFilters() {
    CitizenSearchParams params = new CitizenSearchParams();
    params.setName("bob");
    params.setSpeciesWeight(60.0);
    params.setRoleNames(List.of("admin"));
    Criteria criteria = CitizenCriteriaBuilder.build(params);
    String json = criteria.getCriteriaObject().toJson();
    // Debe contener filtros para name, species.weight y roleNames
    assertTrue(json.contains("name"));
    assertTrue(json.contains("species.weight"));
    assertTrue(json.contains("roleNames"));
  }
}
