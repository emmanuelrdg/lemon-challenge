package com.lemon.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

}
