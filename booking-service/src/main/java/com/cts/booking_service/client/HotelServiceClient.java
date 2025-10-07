package com.cts.booking_service.client;

import com.cts.booking_service.dto.hotel.HotelDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// The 'fallback' attribute points to our fallback component
// The 'url' attribute is REMOVED. Feign will now use the 'name' to look up the service in Eureka.
// The name "HOTEL-SERVICE" must match the 'spring.application.name' of your Hotel microservice.
@FeignClient(name = "HOTEL-SERVICE", fallback = HotelServiceClientFallback.class)
public interface HotelServiceClient {

    @GetMapping("/api/hotels/manager/{managerId}")
    @CircuitBreaker(name = "HOTEL-SERVICE") // The name here can be different, it's for Resilience4j
    List<HotelDto> getHotelsByManagerId(@PathVariable("managerId") String managerId);
}
