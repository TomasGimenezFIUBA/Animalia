package com.tomasgimenez.citizen_command_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
		scanBasePackages = {
			"com.tomasgimenez.citizen_command_service",
			"com.tomasgimenez.citizen_common"
		}
)
@EnableDiscoveryClient
public class CitizenCommandServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitizenCommandServiceApplication.class, args);
	}

}
