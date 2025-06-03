package com.tomasgimenez.citizen_query_service.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Document(collection = "citizens")
public class CitizenDocument {
  @Id
  private String id;
  private String name;
  private boolean hasHumanPet;
  private Species species;
  private List<String> roleNames;
}
