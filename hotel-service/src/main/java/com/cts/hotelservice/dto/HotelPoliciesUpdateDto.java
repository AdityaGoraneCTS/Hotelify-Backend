package com.cts.hotelservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPoliciesUpdateDto {

    @NotNull(message = "Hotel policies are required")
    @Valid
    private HotelPoliciesDto policies;

    private Boolean isFullyRefundable;
    private Boolean hasFreeBreakfast;
    private Boolean reserveNowPayLater;
}
