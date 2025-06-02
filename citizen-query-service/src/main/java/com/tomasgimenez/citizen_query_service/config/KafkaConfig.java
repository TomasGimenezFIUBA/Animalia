package com.tomasgimenez.citizen_query_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;

@EnableKafka
@EnableKafkaRetryTopic
@Configuration
public class KafkaConfig {
}

