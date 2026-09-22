package com.prajan.cinehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinehubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinehubApplication.class, args);
	}

}
