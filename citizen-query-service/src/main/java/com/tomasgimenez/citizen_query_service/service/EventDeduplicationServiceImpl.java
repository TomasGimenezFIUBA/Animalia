package com.tomasgimenez.citizen_query_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EventDeduplicationServiceImpl implements EventDeduplicationService {

  private final StringRedisTemplate redisTemplate;
  private static final Duration TTL = Duration.ofMinutes(15);

  @Override
  public boolean isEventProcessed(String eventId) {
    try {
      Boolean exists = redisTemplate.hasKey(eventId);
      return Boolean.TRUE.equals(exists);
    } catch (Exception e) {
      throw new DatabaseReadException("Failed to check if event is processed: " + eventId, e);
    }
  }

  @Override
  public void markEventAsProcessed(String eventId) {
    try {
      redisTemplate.opsForValue().set(eventId, "1", TTL);
    } catch (Exception e) {
      throw new DatabaseWriteException("Failed to mark event as processed: " + eventId, e);
    }
  }
}

