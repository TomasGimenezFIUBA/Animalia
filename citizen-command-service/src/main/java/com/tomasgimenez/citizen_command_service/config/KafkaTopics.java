package com.tomasgimenez.citizen_command_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "kafka.topic")
@Setter
@Getter
public class KafkaTopics {
    private String citizenCreated;
    private String citizenUpdated;
    private String citizenDeleted;
}
