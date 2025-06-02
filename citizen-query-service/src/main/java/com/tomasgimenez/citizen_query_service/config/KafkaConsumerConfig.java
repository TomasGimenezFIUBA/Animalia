package com.tomasgimenez.citizen_query_service.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.tomasgimenez.citizen_common.kafka.AvroDeserializer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
  private final KafkaTopics kafkaTopics;
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory(
      ConsumerFactory<String, byte[]> consumerFactory,
      DeadLetterPublishingRecoverer recoverer
  ) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
    factory.setConsumerFactory(consumerFactory);

    var backoff = new FixedBackOff(1000L, 3);
    var errorHandler = new DefaultErrorHandler(recoverer, backoff);
    factory.setCommonErrorHandler(errorHandler);

    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    factory.setConcurrency(kafkaTopics.getPartitions());

    return factory;
  }

  @Bean
  public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, byte[]> kafkaTemplate) {
    return new DeadLetterPublishingRecoverer(kafkaTemplate,
        (record, ex) -> new TopicPartition(record.topic() + ".dlt", record.partition()));
  }

  @Bean
  AvroDeserializer avroDeserializer() {
    return new AvroDeserializer();
  }
}

