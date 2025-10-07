package com.cts.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler to provide consistent, structured error responses across the API.
 */
@RestControllerAdvice
public class ApiGlobalExceptionHandler {

    /**
     * Handles validation errors for @Valid annotated request bodies.
     * Returns HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiException handlerForMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                  HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ApiException.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message(errorMessage)
                .build();
    }

    /**
     * Handles the case where a requested resource cannot be found.
     * Returns HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiException handlerForResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        return ApiException.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(e.getMessage())
                .build();
    }

    /**
     * Handles the case where a user tries to register with an email that already exists.
     * Returns HTTP 409 Conflict.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiException handlerForEmailAlreadyExistsException(EmailAlreadyExistsException e, HttpServletRequest request) {
        return ApiException.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.CONFLICT.value())
                .error("Data Conflict")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiException handlerForUsernameAlreadyExistsException(UsernameAlreadyExistsException e, HttpServletRequest request) {
        return ApiException.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.CONFLICT.value())
                .error("Data Conflict")
                .message(e.getMessage())
                .build();
    }

    /**
     * A generic catch-all handler for any other unexpected RuntimeException.
     * This is a safety net. Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiException handlerForGenericRuntimeException(RuntimeException e, HttpServletRequest request) {
        return ApiException.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + e.getMessage())
                .build();
    }
}