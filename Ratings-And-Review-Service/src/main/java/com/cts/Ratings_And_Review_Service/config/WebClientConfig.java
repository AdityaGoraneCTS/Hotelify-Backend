package com.cts.Ratings_And_Review_Service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // This is the magic switch!
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}