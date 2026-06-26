package com.sanviitech.mortextBank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MortextBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(MortextBankApplication.class, args);
	}
}
