package com.tomasgimenez.citizen_query_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;

class RedisQuarantineServiceImplTest {

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private HashOperations<String, Object, Object> hashOps;

  private RedisQuarantineServiceImpl service;

  private static final String CITIZEN_ID = "123";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(redisTemplate.opsForHash()).thenReturn(hashOps);

    service = new RedisQuarantineServiceImpl(redisTemplate);
    service.setFailureThreshold(3);
  }

  @Test
  void isInQuarantine_returnsFalse_whenValueIsNull() {
    when(hashOps.get("citizen:failureCount", CITIZEN_ID)).thenReturn(null);
    assertFalse(service.isInQuarantine(CITIZEN_ID));
  }

  @Test
  void isInQuarantine_returnsFalse_whenValueIsBelowThreshold() {
    when(hashOps.get("citizen:failureCount", CITIZEN_ID)).thenReturn("2");
    assertFalse(service.isInQuarantine(CITIZEN_ID));
  }

  @Test
  void isInQuarantine_returnsTrue_whenValueEqualsThreshold() {
    when(hashOps.get("citizen:failureCount", CITIZEN_ID)).thenReturn("3");
    assertTrue(service.isInQuarantine(CITIZEN_ID));
  }

  @Test
  void isInQuarantine_returnsTrue_whenValueAboveThreshold() {
    when(hashOps.get("citizen:failureCount", CITIZEN_ID)).thenReturn("5");
    assertTrue(service.isInQuarantine(CITIZEN_ID));
  }

  @Test
  void isInQuarantine_returnsFalse_whenValueIsNotANumber() {
    when(hashOps.get("citizen:failureCount", CITIZEN_ID)).thenReturn("invalid");
    assertFalse(service.isInQuarantine(CITIZEN_ID));
  }

  @Test
  void resetQuarantineCounter_deletesFromRedis() {
    service.resetQuarantineCounter(CITIZEN_ID);
    verify(hashOps).delete("citizen:failureCount", CITIZEN_ID);
  }

  @Test
  void recordFailureForCitizen_returnsFalse_whenFailuresBelowThreshold() {
    when(hashOps.increment("citizen:failureCount", CITIZEN_ID, 1)).thenReturn(2L);
    assertFalse(service.recordFailureForCitizen(CITIZEN_ID));
  }

  @Test
  void recordFailureForCitizen_returnsTrue_whenFailuresEqualsThreshold() {
    when(hashOps.increment("citizen:failureCount", CITIZEN_ID, 1)).thenReturn(3L);
    assertTrue(service.recordFailureForCitizen(CITIZEN_ID));
  }

  @Test
  void recordFailureForCitizen_returnsTrue_whenFailuresAboveThreshold() {
    when(hashOps.increment("citizen:failureCount", CITIZEN_ID, 1)).thenReturn(5L);
    assertTrue(service.recordFailureForCitizen(CITIZEN_ID));
  }

  @Test
  void recordFailureForCitizen_returnsFalse_whenRedisReturnsNull() {
    when(hashOps.increment("citizen:failureCount", CITIZEN_ID, 1)).thenReturn(null);
    assertFalse(service.recordFailureForCitizen(CITIZEN_ID));
  }

  @Test
  void isInQuarantine_shouldThrowDatabaseReadException_whenUnexpectedExceptionIsThrown() {
    when(hashOps.get("citizen:failureCount", CITIZEN_ID)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseReadException exception = assertThrows(DatabaseReadException.class,
        () -> service.isInQuarantine(CITIZEN_ID));

    assertEquals("Error accessing Redis for citizen quarantine status: " + CITIZEN_ID, exception.getMessage());
  }

  @Test
  void resetQuarantineCounter_shouldThrowDatabaseWriteException_whenUnexpectedExceptionIsThrown() {
    doThrow(new RuntimeException("Unexpected error")).when(hashOps).delete("citizen:failureCount", CITIZEN_ID);

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> service.resetQuarantineCounter(CITIZEN_ID));

    assertEquals("Failed to reset quarantine counter in Redis for citizen: " + CITIZEN_ID, exception.getMessage());
  }

  @Test
  void recordFailureForCitizen_shouldThrowDatabaseWriteException_whenUnexpectedExceptionIsThrown() {
    when(hashOps.increment("citizen:failureCount", CITIZEN_ID, 1)).thenThrow(new RuntimeException("Unexpected error"));

    DatabaseWriteException exception = assertThrows(DatabaseWriteException.class,
        () -> service.recordFailureForCitizen(CITIZEN_ID));

    assertEquals("Failed to record failure in Redis for citizen: " + CITIZEN_ID, exception.getMessage());
  }
}
