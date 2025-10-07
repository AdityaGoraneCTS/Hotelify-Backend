package com.cts.Ratings_And_Review_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RatingsAndReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatingsAndReviewServiceApplication.class, args);
	}

}
