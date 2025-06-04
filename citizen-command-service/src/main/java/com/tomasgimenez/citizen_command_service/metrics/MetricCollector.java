package com.tomasgimenez.citizen_command_service.metrics;

public interface MetricCollector {
  void reportFailure(String entityType, String exception, String producingFailuresTotal);
  void reportSuccess(String entityType, String producingSuccessTotal);

}
