package com.tomasgimenez.citizen_query_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_common.exception.DatabaseAccessException;
import com.tomasgimenez.citizen_query_service.exception.PersistenceException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Setter
@Slf4j
public class RedisQuarantineServiceImpl implements QuarantineService {

    private static final String FAILURE_COUNT_KEY = "citizen:failureCount";
    private final StringRedisTemplate redisTemplate;

    @Value("${quarantine.failure-threshold:3}")
    private int failureThreshold;

    @Override
    public boolean isInQuarantine(String citizenId) {
        Object value;
        try {
            value = redisTemplate.opsForHash().get(FAILURE_COUNT_KEY, citizenId);

        }catch (Exception e) {
            log.error("Error checking quarantine status for citizen {}: {}", citizenId, e.getMessage(), e);
            throw new DatabaseAccessException(
                "Error accessing Redis for citizen quarantine status due to: " + citizenId, e);
        }
        
        if (value == null) return false;

        try {
            int count = Integer.parseInt(value.toString());
            return count >= failureThreshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void resetQuarantineCounter(String citizenId) {
        try {
            redisTemplate.opsForHash().delete(FAILURE_COUNT_KEY, citizenId);
        } catch (Exception e) {
            log.error("Error resetting quarantine counter for citizen {}: {}", citizenId, e.getMessage(), e);
            throw new PersistenceException(
                "Error resetting quarantine counter for citizen in Redis due to: " + citizenId, e);
        }
    }

    @Override
    public boolean recordFailureForCitizen(String citizenId) {
        try {
            Long failures = redisTemplate.opsForHash().increment(FAILURE_COUNT_KEY, citizenId, 1);
            return failures != null && failures >= failureThreshold;
        } catch (Exception e) {
            log.error("Error recording failure for citizen {}: {}", citizenId, e.getMessage(), e);
            throw new PersistenceException(
                "Error recording failure for citizen in Redis due to: " + citizenId, e);
        }
    }
}
