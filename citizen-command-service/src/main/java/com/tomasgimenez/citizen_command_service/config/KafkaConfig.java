package com.tomasgimenez.citizen_command_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
  private final KafkaProperties kafkaProps;

  @Bean
  public ProducerFactory<String, SpecificRecord> producerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    config.put(ProducerConfig.RETRIES_CONFIG, kafkaProps.getRetries());
    config.put("schema.registry.url", kafkaProps.getSchemaRegistryUrl());
    config.put(ProducerConfig.ACKS_CONFIG, kafkaProps.getAck());
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProps.isIdempotence());
    config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, kafkaProps.getMaxInFlightRequestsPerConnection());
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, SpecificRecord> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}

