package com.cts.hotelservice.exception;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(String id) {
        super("Hotel not found with id: " + id);
    }
}
