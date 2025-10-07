package com.cts.booking_service.config;

import com.cts.booking_service.dto.BookingRequestDTO;
import com.cts.booking_service.models.Booking;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to provide a ModelMapper bean for the application.
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<BookingRequestDTO, Booking>() {
            @Override
            protected void configure() {
                skip().setId(null); // Correct way to skip mapping for id
            }
        });
        return modelMapper;
    }
}
