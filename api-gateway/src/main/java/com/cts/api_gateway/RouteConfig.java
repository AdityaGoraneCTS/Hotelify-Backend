package com.cts.api_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Autowired
    private GatewayApiAuthenticationFilter gatewayApiAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // --- USER-SERVICE Routes ---
                .route("user_service_users_route", // Unique ID
                        r -> r.path("/api/users/**")
                                .filters(f -> f.filter(gatewayApiAuthenticationFilter))
                                .uri("lb://USER-SERVICE"))

                .route("user_service_password_route", // Unique ID
                        r -> r.path("/api/forgotPassword/**")
                                .filters(f -> f.filter(gatewayApiAuthenticationFilter))
                                .uri("lb://USER-SERVICE"))

                // --- HOTEL-SERVICE Routes ---
                .route("hotel_service_hotels_route", // Unique ID
                        r -> r.path("/api/hotels/**")
                                .filters(f -> f.filter(gatewayApiAuthenticationFilter))
                                .uri("lb://HOTEL-SERVICE"))

                // --- BOOKING-SERVICE Routes ---
                .route("booking_service_bookings_route", // Unique ID
                        r -> r.path("/api/bookings/**")
                                .filters(f -> f.filter(gatewayApiAuthenticationFilter))
                                .uri("lb://BOOKING-SERVICE"))

                .route("booking_service_coupons_route", // Unique ID
                        r -> r.path("/api/coupons/**")
                                .filters(f -> f.filter(gatewayApiAuthenticationFilter))
                                .uri("lb://BOOKING-SERVICE"))
                .route("review_and_rating_service_route", // Unique ID
                        r -> r.path("/api/reviews/**")
                                .filters(f -> f.filter(gatewayApiAuthenticationFilter))
                                .uri("lb://RATINGS-AND-REVIEW-SERVICE"))
                .build();
    }
}