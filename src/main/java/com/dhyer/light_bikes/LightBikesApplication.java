package com.dhyer.light_bikes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableScheduling
public class LightBikesApplication {

	public static void main(String[] args) {
		SpringApplication.run(LightBikesApplication.class, args);
	}

	@Bean(name = "Game Store")
	public GameStore gameStore() {
		return new GameStore();
	}
}
