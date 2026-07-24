package com.words.types;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TypeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypeServiceApplication.class, args);
	}

}
