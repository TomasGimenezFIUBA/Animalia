package com.tomasgimenez.citizen_query_service.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.tomasgimenez.citizen_query_service.model.*;
import com.tomasgimenez.citizen_query_service.model.dto.CitizenSearchParams;
import com.tomasgimenez.citizen_query_service.repository.citizen.CitizenCriteriaBuilder;
import com.tomasgimenez.citizen_query_service.repository.citizen.CitizenRepositoryCustomImpl;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;

class CitizenRepositoryCustomImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private CitizenSearchParams searchParams;

  private CitizenRepositoryCustomImpl repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    repository = new CitizenRepositoryCustomImpl(mongoTemplate);
  }

  @Test
  void findByFilters_returnsPagedResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Criteria criteria = Criteria.where("name").is("test");
    Query queryWithPage = new Query(criteria).with(pageable);

    CitizenDocument doc = new CitizenDocument("1331", "Test Citizen", true,
        new Species("1234", "human", 80.0, 1.8), List.of("user"));
    when(mongoTemplate.find(any(Query.class), eq(CitizenDocument.class)))
        .thenReturn(List.of(doc));
    when(mongoTemplate.count(any(Query.class), eq(CitizenDocument.class)))
        .thenReturn(1L);

    try (MockedStatic<CitizenCriteriaBuilder> mockedBuilder = mockStatic(CitizenCriteriaBuilder.class)) {
      mockedBuilder.when(() -> CitizenCriteriaBuilder.build(searchParams)).thenReturn(criteria);

      Page<CitizenDocument> page = repository.findByFilters(searchParams, pageable);

      assertEquals(1, page.getTotalElements());
      assertEquals(doc, page.getContent().get(0));
    }
  }

  @Test
  void update_doesNothing_whenPatchIsEmpty() {
    CitizenPatch emptyPatch = new CitizenPatch(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    repository.update("id123", emptyPatch);
    verify(mongoTemplate, never()).updateFirst(any(), any(), eq(CitizenDocument.class));
  }

  @Test
  void update_updatesFields_whenPatchHasValues() {
    Species species = new Species("12212", "human",90.0, 1.75);
    CitizenPatch patch = new CitizenPatch(
        Optional.of("Bob"),
        Optional.of(true),
        Optional.of(species),
        Optional.of(List.of("admin"))
    );

    repository.update("id123", patch);

    ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
    verify(mongoTemplate).updateFirst(
        eq(Query.query(Criteria.where("_id").is("id123"))),
        updateCaptor.capture(),
        eq(CitizenDocument.class)
    );

    Update update = updateCaptor.getValue();
    var ops = update.getUpdateObject();
    assertTrue(ops.containsKey("$set"));
  }
}
