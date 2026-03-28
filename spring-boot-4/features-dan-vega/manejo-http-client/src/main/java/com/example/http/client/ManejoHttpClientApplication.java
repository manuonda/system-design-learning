package com.example.http.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;


@EnableResilientMethods
@SpringBootApplication
public class ManejoHttpClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManejoHttpClientApplication.class, args);
	}

}
