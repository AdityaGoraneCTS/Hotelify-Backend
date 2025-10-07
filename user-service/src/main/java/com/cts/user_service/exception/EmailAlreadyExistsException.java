package com.cts.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Using @ResponseStatus is a simple way to map this exception to an HTTP status code.
// 409 Conflict is a good choice for a resource that already exists.
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
