package com.WalletProject.WalletProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WalletService {

	public static void main(String[] args) {
		SpringApplication.run(WalletService.class, args);
	}

}
