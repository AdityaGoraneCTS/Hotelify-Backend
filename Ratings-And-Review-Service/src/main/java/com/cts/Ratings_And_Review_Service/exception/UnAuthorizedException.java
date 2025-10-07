package com.cts.Ratings_And_Review_Service.exception;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String msg){
        super(msg);
    }
}
