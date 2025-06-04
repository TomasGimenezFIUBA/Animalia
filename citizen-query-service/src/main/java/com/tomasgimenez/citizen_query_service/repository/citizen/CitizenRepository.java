package com.tomasgimenez.citizen_query_service.repository.citizen;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;

public interface CitizenRepository extends MongoRepository<CitizenDocument, String>, CitizenRepositoryCustom {

}
