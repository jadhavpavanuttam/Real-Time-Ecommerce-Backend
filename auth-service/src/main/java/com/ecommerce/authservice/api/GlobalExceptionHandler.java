package com.ecommerce.authservice.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> bad(IllegalArgumentException ex, HttpServletRequest req) {
        return build( HttpStatus.BAD_REQUEST, ex.getMessage(), req, Map.of() );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var d = ex.getBindingResult().getFieldErrors().stream().collect( Collectors.toMap( e -> e.getField(), e -> String.valueOf( e.getDefaultMessage() ), (a, b) -> a ) );
        return build( HttpStatus.BAD_REQUEST, "Validation failed", req, d );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> other(Exception ex, HttpServletRequest req) {
        return build( HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req, Map.of() );
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus s, String m, HttpServletRequest r, Map<String, String> d) {
        return ResponseEntity.status( s ).body( new ErrorResponse( Instant.now(), s.value(), s.getReasonPhrase(), m, r.getRequestURI(), d ) );
    }
}
