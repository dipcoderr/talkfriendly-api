package com.talkfriendly.api.common.exception;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class) ResponseEntity<ApiError> conflict(ConflictException ex) { return response(HttpStatus.CONFLICT, ex.getMessage(), Map.of()); }
    @ExceptionHandler(UnauthorizedException.class) ResponseEntity<ApiError> unauthorized(UnauthorizedException ex) { return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex) {
        Map<String,String> errors=new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(),e.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST,"Validation failed",errors);
    }
    private ResponseEntity<ApiError> response(HttpStatus status,String message,Map<String,String> errors) { return ResponseEntity.status(status).body(new ApiError(Instant.now(),status.value(),status.getReasonPhrase(),message,errors)); }
}
