package com.grocery.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GroceryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroceryBackendApplication.class, args);
	}

}
