package com.tomasgimenez.citizen_command_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Setter
@Getter
public class KafkaProperties {
    private String bootstrapServers;
    private String schemaRegistryUrl;
    private int retries;
    private String ack;
    private boolean idempotence;
    private int maxInFlightRequestsPerConnection = 1;
}
