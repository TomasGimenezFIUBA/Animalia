package com.tomasgimenez.citizen_command_service.metrics;

import static com.tomasgimenez.citizen_command_service.constants.Metric.PRODUCING_FAILURES_TOTAL;
import static com.tomasgimenez.citizen_command_service.constants.Metric.PRODUCING_SUCCESS_TOTAL;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.context.annotation.Primary;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.tomasgimenez.citizen_command_service.constants.Metric;
import com.tomasgimenez.citizen_command_service.service.CitizenEventProducerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Primary
@Service
public class MetricCitizenEventProducerService implements CitizenEventProducerService {

    private final CitizenEventProducerService delegate;
    private final MetricCollector metricCollector;

    @Override
    public CompletableFuture<SendResult<String, byte[]>> sendCitizenEvent(String key, byte[] payload, String topic, Consumer<SendResult<String, byte[]>> onSuccess) {
        return delegate.sendCitizenEvent(key, payload, topic, onSuccess)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    metricCollector.reportFailure(Metric.CITIZEN_EVENT, exception.getClass().getSimpleName(),
                        PRODUCING_FAILURES_TOTAL);
                } else {
                    metricCollector.reportSuccess(Metric.CITIZEN_EVENT, PRODUCING_SUCCESS_TOTAL);
                }
            });
    }

}
