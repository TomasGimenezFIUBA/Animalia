package com.tomasgimenez.citizen_command_service.cache;

import com.tomasgimenez.citizen_command_service.config.CacheConfig;
import com.tomasgimenez.citizen_command_service.model.entity.CitizenEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleEntity;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;
import com.tomasgimenez.citizen_command_service.model.request.CreateCitizenRequest;
import com.tomasgimenez.citizen_command_service.model.request.UpdateCitizenRequest;
import com.tomasgimenez.citizen_command_service.service.CitizenService;
import com.tomasgimenez.citizen_command_service.service.CitizenServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class CachedCitizenServiceImpl implements CitizenService {

  private final CitizenServiceImpl delegate;
  private final CacheManager cacheManager;

  private static final Set<RoleName> CACHED_ROLES = Set.of(
      RoleName.GENERAL, RoleName.FIRST_MINISTER, RoleName.TREASURER
  );

  @Override
  public CitizenEntity createCitizen(CreateCitizenRequest request) {
    CitizenEntity citizen = delegate.createCitizen(request);

    getCachedRoles(request.roleNames()).forEach(role ->
        updateCache(role, citizens -> citizens.add(citizen))
    );

    return citizen;
  }

  @Override
  public void updateCitizen(UpdateCitizenRequest request) {
    CitizenEntity previous = delegate.getById(request.id());
    delegate.updateCitizen(request);

    if (request.roleNames() == null) return;

    Set<RoleName> previousRoles = getCachedRoles(previous.getRoleEntities()
        .stream().map(RoleEntity::getName)
        .collect(Collectors.toSet()));
    Set<RoleName> newRoles = getCachedRoles(request.roleNames());

    previousRoles.forEach(role ->
        updateCache(role, citizens -> citizens.removeIf(c -> c.getId().equals(request.id())))
    );
    newRoles.forEach(role ->
        updateCache(role, citizens -> citizens.add(delegate.getById(request.id())))
    );
  }

  @Override
  public void deleteCitizen(UUID id) {
    CitizenEntity citizen;
    try {
      citizen = delegate.getById(id);
    } catch (EntityNotFoundException e) {
      return;
    }

    delegate.deleteCitizen(id);

    getCachedRoles(citizen.getRoleEntities()
        .stream().map(RoleEntity::getName)
        .collect(Collectors.toSet())
    ).forEach(role ->
        updateCache(role, citizens -> citizens.removeIf(c -> c.getId().equals(id)))
    );
  }

  @Override
  public Set<CitizenEntity> createCitizens(List<CreateCitizenRequest> requests) {
    Set<CitizenEntity> created = delegate.createCitizens(requests);

    Map<RoleName, Set<CitizenEntity>> citizensByRole = new EnumMap<>(RoleName.class);
    for (CitizenEntity citizen : created) {
      for (RoleName role : getCachedRoles(citizen.getRoleEntities().stream()
          .map(RoleEntity::getName)
          .collect(Collectors.toSet()))) {
        citizensByRole.computeIfAbsent(role, r -> new HashSet<>()).add(citizen);
      }
    }

    citizensByRole.forEach((role, newCitizens) ->
        updateCache(role, existing -> existing.addAll(newCitizens))
    );

    return created;
  }

  @Override
  public Set<CitizenEntity> getCitizensByRoleName(RoleName roleName) {
    var cache = cacheManager.getCache(CacheConfig.CITIZENS_CACHE);
    if (cache != null) {
      Set<CitizenEntity> cachedCitizens = cache.get(roleName, Set.class);
      if (cachedCitizens != null) {
        log.debug("Returning cached citizens for role '{}'", roleName);
        return cachedCitizens;
      }
    }

    return delegate.getCitizensByRoleName(roleName);
  }

  private void updateCache(RoleName role, Consumer<Set<CitizenEntity>> modifier) {
    var cache = cacheManager.getCache(CacheConfig.CITIZENS_CACHE);
    if (cache == null) {
      log.warn("Cache '{}' not found, cannot update citizens for role '{}'", CacheConfig.CITIZENS_CACHE, role);
      return;
    }

    Set<CitizenEntity> citizens = new HashSet<>(getCitizensByRoleName(role));
    modifier.accept(citizens);
    cache.put(role, citizens);
  }

  private Set<RoleName> getCachedRoles(Collection<RoleName> roles) {
    return roles.stream()
        .filter(CACHED_ROLES::contains)
        .collect(Collectors.toSet());
  }
}
