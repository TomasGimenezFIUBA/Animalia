package com.tomasgimenez.citizen_command_service.service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.kafka.support.SendResult;

public interface CitizenEventProducerService {
  CompletableFuture<SendResult<String, byte[]>> sendCitizenEvent(String key, byte[] payload, String topic, Consumer<SendResult<String, byte[]>> onSuccess);
}