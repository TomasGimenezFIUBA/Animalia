package com.tomasgimenez.citizen_query_service.controller;

import com.tomasgimenez.citizen_query_service.model.CitizenDocument;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.service.CitizenService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CitizenControllerTest {

  private final CitizenService citizenService = mock(CitizenService.class);
  private final CitizenController controller = new CitizenController(citizenService);

  @Test
  void getById_returnsCitizen() {
    CitizenDocument expected = CitizenDocument.builder().
        name("Tomas").
        id("abc123").
        build();
    when(citizenService.findById("abc123")).thenReturn(expected);

    CitizenDocument result = controller.getById("abc123");

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void search_appliesFiltersCorrectly() {
    Page<CitizenDocument> expectedPage = new PageImpl<>(List.of());
    when(citizenService.findByFilters(any(), any())).thenReturn(expectedPage);

    Page<CitizenDocument> result = controller.search(
        2, 5, "Tomas", "Human", 80.0, 1.8, List.of("ADMIN", "USER"), true
    );

    assertThat(result).isEqualTo(expectedPage);

    ArgumentCaptor<CitizenSearchParams> paramsCaptor = ArgumentCaptor.forClass(CitizenSearchParams.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(citizenService).findByFilters(paramsCaptor.capture(), pageableCaptor.capture());

    CitizenSearchParams params = paramsCaptor.getValue();
    Pageable pageable = pageableCaptor.getValue();

    assertThat(params.getName()).isEqualTo("Tomas");
    assertThat(params.getSpeciesName()).isEqualTo("Human");
    assertThat(params.getSpeciesWeight()).isEqualTo(80.0);
    assertThat(params.getSpeciesHeight()).isEqualTo(1.8);
    assertThat(params.getRoleNames()).containsExactly("ADMIN", "USER");
    assertThat(params.getHasHumanPet()).isTrue();
    assertThat(pageable.getPageNumber()).isEqualTo(2);
    assertThat(pageable.getPageSize()).isEqualTo(5);
  }

  @Test
  void search_usesDefaultPagination() {
    when(citizenService.findByFilters(any(), any())).thenReturn(Page.empty());

    controller.search(
        null, null, null, null, null, null, null, null
    );

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(citizenService).findByFilters(any(), pageableCaptor.capture());

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isEqualTo(0);
    assertThat(pageable.getPageSize()).isEqualTo(10);
  }
}
