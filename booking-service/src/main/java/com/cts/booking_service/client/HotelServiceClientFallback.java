package com.cts.booking_service.client;

import com.cts.booking_service.dto.hotel.HotelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelServiceClientFallback implements HotelServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(HotelServiceClientFallback.class);

    @Override
    public List<HotelDto> getHotelsByManagerId(String managerId) {
        // This is the fallback logic. It's executed when the Hotel Service is down.
        logger.warn("Fallback: Hotel Service is unavailable. Returning empty list for managerId: {}", managerId);
        // We return an empty list to prevent the Booking Service from crashing.
        return List.of();
    }
}
