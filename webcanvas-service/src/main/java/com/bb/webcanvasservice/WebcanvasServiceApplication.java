package com.bb.webcanvasservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class WebcanvasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcanvasServiceApplication.class, args);
	}

}
