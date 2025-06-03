package com.tomasgimenez.citizen_query_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class EventDeduplicationServiceImplTest {

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOps;

  private EventDeduplicationServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    service = new EventDeduplicationServiceImpl(redisTemplate);
  }

  @Test
  void isEventProcessed_returnsTrue_whenKeyExists() {
    when(redisTemplate.hasKey("event123")).thenReturn(true);
    assertTrue(service.isEventProcessed("event123"));
  }

  @Test
  void isEventProcessed_returnsFalse_whenKeyDoesNotExist() {
    when(redisTemplate.hasKey("event123")).thenReturn(false);
    assertFalse(service.isEventProcessed("event123"));
  }

  @Test
  void isEventProcessed_returnsFalse_whenRedisReturnsNull() {
    when(redisTemplate.hasKey("event123")).thenReturn(null);
    assertFalse(service.isEventProcessed("event123"));
  }

  @Test
  void markEventAsProcessed_storesValueWithTTL() {
    service.markEventAsProcessed("event123");
    verify(valueOps).set("event123", "1", Duration.ofMinutes(15));
  }
}
