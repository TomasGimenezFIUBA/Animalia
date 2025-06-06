package com.tomasgimenez.citizen_command_service.runner;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tomasgimenez.citizen_command_service.config.KafkaProperties;
import com.tomasgimenez.citizen_command_service.config.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
* This class is responsible for creating Kafka topics at application startup.
* It is an easy way to ensure that the necessary topics are created for the demo
* */

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTopicCreator implements CommandLineRunner {

  private final KafkaProperties kafkaProps;
  private final KafkaTopics kafkaTopics;

  @Override
  public void run(String... args) {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
    try (AdminClient admin = AdminClient.create(config)) {
      List<String> topicsToCreate = List.of(
          kafkaTopics.getCitizenEvent(),
          kafkaTopics.getCitizenEventDeadLetter(),
          kafkaTopics.getCitizenQuarantine()
      );

      Set<String> existing = admin.listTopics().names().get();

      for (String topic : topicsToCreate) {
        if (!existing.contains(topic)) {
          admin.createTopics(List.of(new NewTopic(topic, kafkaTopics.getPartitions(), kafkaTopics.getReplicationFactor())));
          log.info("Topic created: " + topic);
        }
      }

    } catch (Exception e) {
      log.error("Error while creating topics: " + e.getMessage());
    }
  }
}
