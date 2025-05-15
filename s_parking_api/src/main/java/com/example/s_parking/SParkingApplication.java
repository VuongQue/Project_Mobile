package com.example.s_parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SParkingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SParkingApplication.class, args);
	}

}
