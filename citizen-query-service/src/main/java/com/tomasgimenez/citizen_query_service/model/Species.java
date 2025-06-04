package com.tomasgimenez.citizen_query_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Species {
  private String id;
  private String name;
  private double weight;
  private double height;
}
