package com.cts.booking_service.exception;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String msg){
        super(msg);
    }
}
