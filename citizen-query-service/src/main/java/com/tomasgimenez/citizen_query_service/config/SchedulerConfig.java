package com.tomasgimenez.citizen_query_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/*
* This class configures a ThreadPoolTaskScheduler bean for scheduling tasks in the application.
* It allows kafka retry tasks to be executed in a separate thread pool.
* */
@Configuration
public class SchedulerConfig {
  @Value("${scheduler.pool-size:4}")
  private int poolSize;
  @Bean
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(poolSize);
    scheduler.setThreadNamePrefix("retry-task-");
    scheduler.initialize();
    return scheduler;
  }
}