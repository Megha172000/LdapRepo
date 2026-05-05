package com.example.demo;

import com.example.demo.service.LdapTestService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	LdapTestService ldapTestService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
