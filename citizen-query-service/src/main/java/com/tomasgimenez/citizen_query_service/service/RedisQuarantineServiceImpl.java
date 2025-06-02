package com.tomasgimenez.citizen_query_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisQuarantineServiceImpl implements QuarantineService {

    private static final String FAILURE_COUNT_KEY = "citizen:failureCount";
    private final StringRedisTemplate redisTemplate;

    @Value("${quarantine.failure-threshold:3}")
    private int failureThreshold;

    @Override
    public boolean isInQuarantine(String citizenId) {
        Object value = redisTemplate.opsForHash().get(FAILURE_COUNT_KEY, citizenId);
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
        redisTemplate.opsForHash().delete(FAILURE_COUNT_KEY, citizenId);
    }

    @Override
    public boolean recordFailureForCitizen(String citizenId) {
        Long failures = redisTemplate.opsForHash().increment(FAILURE_COUNT_KEY, citizenId, 1);
        return failures != null && failures >= failureThreshold;
    }
}
