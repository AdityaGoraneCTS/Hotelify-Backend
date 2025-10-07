package com.cts.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiException {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}