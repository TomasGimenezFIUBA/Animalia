package com.tomasgimenez.citizen_command_service.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.kafka.support.SendResult;

import com.tomasgimenez.citizen_common.exception.MessageProductionException;

public interface CitizenEventProducer {
  CompletableFuture<SendResult<String, byte[]>> sendCitizenEvent(String key, byte[] payload, String topic, Consumer<SendResult<String, byte[]>> onSuccess)
      throws MessageProductionException;
}