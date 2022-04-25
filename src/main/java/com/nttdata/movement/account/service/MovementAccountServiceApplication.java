package com.nttdata.movement.account.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MovementAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovementAccountServiceApplication.class, args);
	}

}
