package com.gruapim.collaboration.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message) {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), null);
    }

    public static ErrorResponse ofValidation(int status, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(status, "Validation Error", "Campos inválidos", path, Instant.now(), fieldErrors);
    }
}
