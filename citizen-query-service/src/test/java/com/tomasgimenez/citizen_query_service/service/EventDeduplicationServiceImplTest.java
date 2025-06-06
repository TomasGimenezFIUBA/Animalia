package com.tomasgimenez.citizen_query_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;

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

  @Test
  void isEventProcessed_shouldThrowDatabaseAccessException_whenUnexpectedExceptionIsThrown() {
    when(redisTemplate.hasKey("event123")).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseReadException exception = assertThrows(DatabaseReadException.class,
        () -> service.isEventProcessed("event123"));

    assertEquals("Failed to check if event is processed: event123", exception.getMessage());
  }

  @Test
  void markEventAsProcessed_shouldThrowDatabaseWriteException_whenUnexpectedExceptionIsThrown() {
    doThrow(new RuntimeException("Unexpected error")).when(valueOps).set("event123", "1", Duration.ofMinutes(15));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> service.markEventAsProcessed("event123"));

    assertEquals("Failed to mark event as processed: event123", exception.getMessage());
  }}
