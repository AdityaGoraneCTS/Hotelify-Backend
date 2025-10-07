package com.cts.hotelservice.exception;

public class InvalidHotelDataException extends RuntimeException {
    public InvalidHotelDataException(String message) {
        super(message);
    }
}
