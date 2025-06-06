package com.tomasgimenez.citizen_query_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_common.exception.DatabaseReadException;
import com.tomasgimenez.citizen_common.exception.DatabaseWriteException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class RedisQuarantineServiceImpl implements QuarantineService {

    private static final String REDIS_HASH_KEY = "citizen:failureCount";

    private final StringRedisTemplate redisTemplate;

    @Value("${quarantine.failure-threshold:3}")
    private int failureThreshold;

    @Override
    public boolean isInQuarantine(String citizenId) {
        try {
            String countStr = (String) redisTemplate.opsForHash().get(REDIS_HASH_KEY, citizenId);
            if (countStr == null) return false;

            int count = Integer.parseInt(countStr);
            return count >= failureThreshold;
        } catch (NumberFormatException e) {
            log.warn("Invalid failure count format for citizen {} in Redis", citizenId);
            return false;
        } catch (Exception e) {
            log.error("Error checking quarantine status for citizen {}: {}", citizenId, e.getMessage(), e);
            throw new DatabaseReadException(
                "Error accessing Redis for citizen quarantine status: " + citizenId, e);
        }
    }

    @Override
    public void resetQuarantineCounter(String citizenId) {
        try {
            redisTemplate.opsForHash().delete(REDIS_HASH_KEY, citizenId);
            log.debug("Reset quarantine counter for citizen {}", citizenId);
        } catch (Exception e) {
            log.error("Error resetting quarantine counter for citizen {}: {}", citizenId, e.getMessage(), e);
            throw new DatabaseWriteException(
                "Failed to reset quarantine counter in Redis for citizen: " + citizenId, e);
        }
    }

    @Override
    public boolean recordFailureForCitizen(String citizenId) {
        try {
            Long failureCount = redisTemplate.opsForHash().increment(REDIS_HASH_KEY, citizenId, 1);
            return failureCount != null && failureCount >= failureThreshold;
        } catch (Exception e) {
            log.error("Error recording failure for citizen {}: {}", citizenId, e.getMessage(), e);
            throw new DatabaseWriteException(
                "Failed to record failure in Redis for citizen: " + citizenId, e);
        }
    }
}
