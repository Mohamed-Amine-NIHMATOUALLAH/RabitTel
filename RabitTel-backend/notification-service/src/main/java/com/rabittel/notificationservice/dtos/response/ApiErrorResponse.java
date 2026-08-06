package com.rabittel.notificationservice.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error envelope returned by the {@code GlobalExceptionHandler}.
 *
 * <p>{@code details} is only serialized when non-null, e.g. for
 * validation errors where each field violation is listed.</p>
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /** HTTP status code. */
    private final int status;

    /** Short, machine-readable error code. */
    private final String error;

    /** Human-readable description of what went wrong. */
    private final String message;

    /** Request path that triggered the error. */
    private final String path;

    /** Timestamp of the error. */
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /** Optional list of field-level validation messages. */
    private final List<String> details;
}
