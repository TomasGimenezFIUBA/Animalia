package com.tomasgimenez.citizen_query_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
@Setter
@Getter
public class KafkaTopics {
    private String citizenEvent;
    private String citizenQuarantine;
    private int partitions;
    private short replicationFactor;
}
