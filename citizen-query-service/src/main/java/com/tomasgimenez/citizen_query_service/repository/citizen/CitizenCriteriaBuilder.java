package com.tomasgimenez.citizen_query_service.repository.citizen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;

public class CitizenCriteriaBuilder {
  private CitizenCriteriaBuilder() {
    // Private constructor to prevent instantiation
  }

  public static Criteria build(CitizenSearchParams params) {
    List<Criteria> filters = new ArrayList<>();

    addRegexFilter(filters, "name", params.getName());
    addRegexFilter(filters, "species.name", params.getSpeciesName());
    addEqualityFilter(filters, "species.weight", params.getSpeciesWeight());
    addEqualityFilter(filters, "species.height", params.getSpeciesHeight());
    addInFilter(filters, "roleNames", params.getRoleNames());
    addEqualityFilter(filters, "hasHumanPet", params.getHasHumanPet());

    return filters.isEmpty() ? new Criteria() : new Criteria().andOperator(filters.toArray(new Criteria[0]));
  }

  private static void addRegexFilter(List<Criteria> filters, String field, String value) {
    if (value != null) {
      filters.add(Criteria.where(field).regex("(?i)" + value));
    }
  }

  private static void addEqualityFilter(List<Criteria> filters, String field, Object value) {
    if (value != null) {
      filters.add(Criteria.where(field).is(value));
    }
  }

  private static void addInFilter(List<Criteria> filters, String field, List<String> values) {
    if (values != null && !values.isEmpty()) {
      filters.add(Criteria.where(field).in(values));
    }
  }
}