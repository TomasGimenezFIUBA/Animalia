package com.tomasgimenez.citizen_command_service.metrics;

import static com.tomasgimenez.citizen_command_service.constants.Metric.ENTITY_TYPE;
import static com.tomasgimenez.citizen_command_service.constants.Metric.EXCEPTION_TYPE;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MicrometerMetricCollector implements MetricCollector {
  private final MeterRegistry meterRegistry;
  @Override
  public void reportFailure(String entityType, String exception, String producingFailuresTotal) {
    meterRegistry
        .counter(producingFailuresTotal,
            ENTITY_TYPE, entityType,
            EXCEPTION_TYPE, exception)
        .increment();
  }

  @Override
  public void reportSuccess(String entityType, String producingSuccessTotal) {
    meterRegistry
        .counter(producingSuccessTotal, ENTITY_TYPE, entityType)
        .increment();
  }
}
