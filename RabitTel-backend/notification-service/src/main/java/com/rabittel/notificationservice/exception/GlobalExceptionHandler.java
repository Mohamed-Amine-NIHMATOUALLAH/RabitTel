package com.rabittel.notificationservice.exception;

import com.rabittel.notificationservice.dtos.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Centralised exception handler for all REST controllers.
 *
 * <p>Maps domain exceptions and Spring validation failures to a
 * consistent {@link ApiErrorResponse} envelope so clients always
 * receive the same JSON structure regardless of what went wrong.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------------------------------
    // 404 — Resource not found
    // ----------------------------------------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found: {} — path: {}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request));
    }

    // ----------------------------------------------------------------
    // 400 — Bean validation failures (@Valid / @Validated)
    // ----------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        log.warn("Validation failed on {}: {}", request.getRequestURI(), details);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Request validation failed. Please check the details.")
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // ----------------------------------------------------------------
    // 400 — Illegal arguments (e.g. unsupported notification type)
    // ----------------------------------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("Illegal argument: {} — path: {}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity
                .badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request));
    }

    // ----------------------------------------------------------------
    // 400 — Path/query parameter type mismatch (e.g. invalid UUID)
    // ----------------------------------------------------------------

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String message = String.format(
                "Parameter '%s' must be of type '%s'.",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        log.warn("Type mismatch on {}: {}", request.getRequestURI(), message);

        return ResponseEntity
                .badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", message, request));
    }

    // ----------------------------------------------------------------
    // 500 — Unexpected / uncaught
    // ----------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .internalServerError()
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        request
                ));
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------

    private ApiErrorResponse buildError(
            HttpStatus status,
            String errorCode,
            String message,
            HttpServletRequest request
    ) {
        return ApiErrorResponse.builder()
                .status(status.value())
                .error(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}
