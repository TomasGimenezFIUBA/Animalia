package com.tomasgimenez.citizen_command_service.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tomasgimenez.citizen_command_service.model.entity.RoleName;

@Configuration
@EnableCaching
public class CacheConfig {
  public static final String ROLES_CACHE = "roles";
  public static final String SPECIES_CACHE = "species";
  public static final String CITIZENS_CACHE = "citizens";

  @Bean
  public CacheManager cacheManager() {
    List<CaffeineCache> caches = new ArrayList<>();
    caches.add(rolesCache());
    caches.add(speciesCache());
    caches.add(citizensCache());
    SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(caches);
    return manager;
  }

  private static CaffeineCache buildCache (String name, long ttl, TimeUnit ttlUnit, long size) {
    return new CaffeineCache(name, Caffeine.newBuilder()
        .expireAfterWrite(ttl, ttlUnit)
        .maximumSize(size)
        .build());
  }

  private static CaffeineCache rolesCache() {
    return buildCache("roles", 2, TimeUnit.HOURS, RoleName.values().length);
  }

  private static CaffeineCache speciesCache() {
    return buildCache("species", 2, TimeUnit.HOURS, 50);
  }

  private static CaffeineCache citizensCache() {
    return buildCache("citizens", 2, TimeUnit.HOURS, 20);
  }
}
