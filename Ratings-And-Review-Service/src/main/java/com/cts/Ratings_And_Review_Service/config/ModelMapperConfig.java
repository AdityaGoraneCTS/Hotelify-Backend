package com.cts.Ratings_And_Review_Service.config;

import com.cts.Ratings_And_Review_Service.dto.ReviewRequestDTO;
import com.cts.Ratings_And_Review_Service.entity.Review;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // 1. Set a stricter matching strategy. This is a good practice in general.
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // 2. Add a specific mapping rule for ReviewRequestDTO -> Review.
        //    This rule explicitly tells ModelMapper to SKIP setting the 'id' field.
        modelMapper.typeMap(ReviewRequestDTO.class, Review.class).addMappings(mapper -> {
            mapper.skip(Review::setId);
        });

        return modelMapper;
    }
}