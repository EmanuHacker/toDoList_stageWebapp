package com.dirignani.webapp;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebappApplication {
	
	public static final String port = "8082";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WebappApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", port));
        app.run(args);
	}

}
