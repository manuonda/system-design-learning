package com.manuonda.blog.agent;

import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.shell.command.annotation.Command;


//@EnableConfigurationProperties(BlogAgentProperties.class)
@ConfigurationPropertiesScan
@SpringBootApplication
public class BlogAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogAgentApplication.class, args);
	}

}
