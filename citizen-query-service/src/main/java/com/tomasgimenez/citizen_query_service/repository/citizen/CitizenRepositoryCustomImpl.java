package com.tomasgimenez.citizen_query_service.repository.citizen;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.CitizenPatch;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CitizenRepositoryCustomImpl implements CitizenRepositoryCustom{
  private final MongoTemplate mongoTemplate;

  @Override
  public Page<CitizenDocument> findByFilters(CitizenSearchParams params, Pageable pageable) {
    var criteria = CitizenCriteriaBuilder.build(params);
    var query = new Query(criteria);
    query.with(pageable);

    List<CitizenDocument> results = mongoTemplate.find(query, CitizenDocument.class);
    long total = mongoTemplate.count(Query.query(criteria), CitizenDocument.class);

    return new PageImpl<>(results, pageable, total);
  }

  @Override
  public void update(String id, CitizenPatch citizenPatch) {
    Update update = new Update();

    citizenPatch.name().ifPresent(name -> update.set("name", name));
    citizenPatch.hasHumanPet().ifPresent(hp -> update.set("hasHumanPet", hp));
    citizenPatch.species().ifPresent(species -> update.set("species", species.toString()));
    citizenPatch.roleNames().ifPresent(roleNames -> update.set("roleNames", roleNames));

    if (!update.getUpdateObject().isEmpty()) {
      mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), update, CitizenDocument.class);
    }
  }
}
