package com.tomasgimenez.citizen_query_service.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.service.CitizenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/citizens")
@RequiredArgsConstructor
@Tag(name = "Citizen API", description = "Endpoints for querying citizens")
public class CitizenController {

  private final CitizenService citizenService;

  @GetMapping("/{id}")
  @Operation(summary = "Find citizen by ID")
  public CitizenDocument getById(@PathVariable String id) {
    return citizenService.findById(id);
  }

  @GetMapping
  @Operation(summary = "Find citizens by filters")
  public Page<CitizenDocument> search(
      @Parameter(description = "Page number", example = "0", required = false) Integer page,
      @Parameter(description = "Page size", example = "10", required = false) Integer size,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String speciesName,
      @RequestParam(required = false) Double speciesWeight,
      @RequestParam(required = false) Double speciesHeight,
      @RequestParam(required = false) List<String> roleNames,
      @RequestParam(required = false) Boolean hasHumanPet
  ) {
    CitizenSearchParams params = new CitizenSearchParams(
        name,
        speciesName,
        speciesWeight,
        speciesHeight,
        roleNames,
        hasHumanPet
    );
    Pageable pageable = Pageable.ofSize(size != null ? size : 10).withPage(page != null ? page : 0);

    return citizenService.findByFilters(params, pageable);
  }
}
