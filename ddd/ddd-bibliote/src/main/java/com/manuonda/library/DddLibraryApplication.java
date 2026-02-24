package com.manuonda.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DddLibraryApplication {

	static void main(String[] args) {
		SpringApplication.run(DddLibraryApplication.class, args);
	}

}
