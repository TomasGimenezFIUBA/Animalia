package com.tomasgimenez.citizen_query_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
		scanBasePackages = {
			"com.tomasgimenez.citizen_query_service",
			"com.tomasgimenez.citizen_common"
		}
)
@EnableDiscoveryClient
public class CitizenQueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitizenQueryServiceApplication.class, args);
	}

}
