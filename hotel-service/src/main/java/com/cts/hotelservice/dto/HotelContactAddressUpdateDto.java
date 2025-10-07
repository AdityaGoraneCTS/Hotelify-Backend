package com.cts.hotelservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelContactAddressUpdateDto {

    @NotNull(message = "Address is required")
    @Valid
    private AddressDto address;

    @NotNull(message = "Contact is required")
    @Valid
    private ContactDto contact;
}
