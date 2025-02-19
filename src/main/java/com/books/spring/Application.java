package com.books.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.books.spring.Model")
@EnableJpaRepositories("com.books.spring.Repository")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}