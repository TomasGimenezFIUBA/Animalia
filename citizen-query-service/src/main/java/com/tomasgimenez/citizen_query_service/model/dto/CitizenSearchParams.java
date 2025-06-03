package com.tomasgimenez.citizen_query_service.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CitizenSearchParams {

    private String name;

    private String speciesName;

    private Double speciesWeight;

    private Double speciesHeight;

    private List<String> roleNames;

    private Boolean hasHumanPet;
}
