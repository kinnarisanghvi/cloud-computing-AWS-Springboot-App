package com.csye6225.spring2019;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NotesApplication {


	public static void main(String[] args) {
		SpringApplication.run(NotesApplication.class, args);
	}
}
