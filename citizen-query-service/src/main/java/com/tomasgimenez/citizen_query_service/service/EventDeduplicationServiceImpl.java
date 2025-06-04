package com.tomasgimenez.citizen_query_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EventDeduplicationServiceImpl implements EventDeduplicationService {

  private final StringRedisTemplate redisTemplate;
  private static final Duration TTL = Duration.ofMinutes(15);

  @Override
  public boolean isEventProcessed(String eventId) {
    Boolean exists = redisTemplate.hasKey(eventId);
    return Boolean.TRUE.equals(exists);
  }

  @Override
  public void markEventAsProcessed(String eventId) {
    redisTemplate.opsForValue().set(eventId, "1", TTL);
  }
}

