package com.webflux.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StudyApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(StudyApplication.class);
		log.info(springApplication.getWebApplicationType().toString());

		springApplication.run(args);
	}

}
