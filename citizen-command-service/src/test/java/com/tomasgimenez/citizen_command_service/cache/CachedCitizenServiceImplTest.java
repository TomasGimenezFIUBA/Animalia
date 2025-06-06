package com.tomasgimenez.citizen_command_service.cache;

import com.tomasgimenez.citizen_command_service.config.CacheConfig;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.service.CitizenServiceImpl;
import com.tomasgimenez.citizen_command_service.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CachedCitizenServiceImplTest {

  private CitizenServiceImpl delegate;
  private CachedCitizenServiceImpl cachedService;
  private Cache cache;

  @BeforeEach
  void setup() {
    delegate = mock(CitizenServiceImpl.class);
    CacheManager cacheManager = mock(CacheManager.class);
    cache = mock(Cache.class);
    when(cacheManager.getCache(CacheConfig.CITIZENS_CACHE)).thenReturn(cache);
    cachedService = new CachedCitizenServiceImpl(delegate, cacheManager);
  }

  private RoleEntity createRole(RoleName roleName) {
    RoleEntity role = new RoleEntity();
    role.setId(UUID.randomUUID());
    role.setName(roleName);
    return role;
  }

  private CitizenEntity createCitizen(UUID id, String name, boolean hasHumanPet, RoleName... roleNames) {
    Set<RoleEntity> roles = new HashSet<>();
    for (RoleName rn : roleNames) {
      roles.add(createRole(rn));
    }
    return CitizenEntity.builder()
        .id(id != null ? id : UUID.randomUUID())
        .name(name)
        .hasHumanPet(hasHumanPet)
        .roleEntities(roles)
        .build();
  }

  @Test
  void createCitizen_shouldUpdateCacheForCachedRoles() {
    CreateCitizenRequest request = new CreateCitizenRequest("John", UUID.randomUUID(), true, Set.of(RoleName.GENERAL));
    CitizenEntity entity = createCitizen(null, "John", true, RoleName.GENERAL);

    when(delegate.createCitizen(request)).thenReturn(entity);
    when(cache.get(RoleName.GENERAL, Set.class)).thenReturn(Set.of());

    cachedService.createCitizen(request);

    verify(cache).put(eq(RoleName.GENERAL), argThat(set -> ((Set<?>) set).contains(entity)));
  }

  @Test
  void updateCitizen_shouldRemoveAndAddFromCache() {
    UUID id = UUID.randomUUID();
    CitizenEntity oldCitizen = createCitizen(id, "John", true, RoleName.GENERAL);
    CitizenEntity newCitizen = createCitizen(id, "John Updated", true, RoleName.TREASURER);
    UpdateCitizenRequest request = new UpdateCitizenRequest(id, "John Updated", UUID.randomUUID(), true, Set.of(RoleName.TREASURER));

    when(delegate.getById(id)).thenReturn(oldCitizen).thenReturn(newCitizen);
    when(cache.get(RoleName.GENERAL, Set.class)).thenReturn(new HashSet<>(Set.of(oldCitizen)));
    when(cache.get(RoleName.TREASURER, Set.class)).thenReturn(Set.of());

    cachedService.updateCitizen(request);

    verify(cache).put(eq(RoleName.GENERAL), argThat(set -> ((Set<?>) set).isEmpty()));
    verify(cache).put(eq(RoleName.TREASURER), argThat(set -> ((Set<?>) set).contains(newCitizen)));
  }

  @Test
  void updateCitizen_shouldDoNothingWhenRolesNull() {
    UUID id = UUID.randomUUID();
    UpdateCitizenRequest request = new UpdateCitizenRequest(id, "John", UUID.randomUUID(), false, null);
    CitizenEntity oldCitizen = createCitizen(id, "John", false, RoleName.GENERAL);

    when(delegate.getById(id)).thenReturn(oldCitizen);

    cachedService.updateCitizen(request);

    verify(cache, never()).put(any(), any());
  }

  @Test
  void deleteCitizen_shouldRemoveFromCache() {
    UUID id = UUID.randomUUID();
    CitizenEntity citizen = createCitizen(id, "John", true, RoleName.GENERAL);

    when(delegate.getById(id)).thenReturn(citizen);
    when(cache.get(RoleName.GENERAL, Set.class)).thenReturn(new HashSet<>(Set.of(citizen)));

    cachedService.deleteCitizen(id);

    verify(delegate).deleteCitizen(id);
    verify(cache).put(eq(RoleName.GENERAL), argThat(set -> ((Set<?>) set).isEmpty()));
  }

  @Test
  void deleteCitizen_shouldNotThrow_whenCitizenNotFound() {
    UUID id = UUID.randomUUID();
    doThrow(EntityNotFoundException.class).when(delegate).getById(id);

    assertDoesNotThrow(() -> cachedService.deleteCitizen(id));

    verify(delegate, never()).deleteCitizen(id);
    verify(cache, never()).put(any(), any());
  }

  @Test
  void createCitizens_shouldGroupAndUpdateCache() {
    CreateCitizenRequest req1 = new CreateCitizenRequest("A", UUID.randomUUID(), true, Set.of(RoleName.GENERAL));
    CreateCitizenRequest req2 = new CreateCitizenRequest("B", UUID.randomUUID(), false, Set.of(RoleName.FIRST_MINISTER));
    CitizenEntity citizen1 = createCitizen(null, "A", true, RoleName.GENERAL);
    CitizenEntity citizen2 = createCitizen(null, "B", false, RoleName.FIRST_MINISTER);

    when(delegate.createCitizens(List.of(req1, req2))).thenReturn(Set.of(citizen1, citizen2));
    when(cache.get(RoleName.GENERAL, Set.class)).thenReturn(Set.of());
    when(cache.get(RoleName.FIRST_MINISTER, Set.class)).thenReturn(Set.of());

    cachedService.createCitizens(List.of(req1, req2));

    verify(cache).put(eq(RoleName.GENERAL), argThat(set -> ((Set<?>) set).contains(citizen1)));
    verify(cache).put(eq(RoleName.FIRST_MINISTER), argThat(set -> ((Set<?>) set).contains(citizen2)));
  }

  @Test
  void getCitizensByRoleName_shouldReturnFromCacheIfExists() {
    Set<CitizenEntity> cached = Set.of(createCitizen(null, "X", false, RoleName.GENERAL));
    when(cache.get(RoleName.GENERAL, Set.class)).thenReturn(cached);

    Set<CitizenEntity> result = cachedService.getCitizensByRoleName(RoleName.GENERAL);

    assertEquals(cached, result);
    verify(delegate, never()).getCitizensByRoleName(any());
  }

  @Test
  void getCitizensByRoleName_shouldDelegateIfCacheEmpty() {
    Set<CitizenEntity> expected = Set.of(createCitizen(null, "Y", true, RoleName.GENERAL));
    when(cache.get(RoleName.GENERAL, Set.class)).thenReturn(null);
    when(delegate.getCitizensByRoleName(RoleName.GENERAL)).thenReturn(expected);

    Set<CitizenEntity> result = cachedService.getCitizensByRoleName(RoleName.GENERAL);

    assertEquals(expected, result);
  }
}
