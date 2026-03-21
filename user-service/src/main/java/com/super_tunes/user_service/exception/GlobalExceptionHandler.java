package com.super_tunes.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildError(HttpStatus status,String message, HttpServletRequest request){
        ApiError apiError=new ApiError(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURL().toString()
        );
        return ResponseEntity.status(status).body(apiError);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex,HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(),request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        String message=ex.getBindingResult().getFieldErrors().stream().
                        findFirst()
                        .map(error->error.getField()+": "+error.getDefaultMessage())
                        .orElse("Validation failed");
        return buildError(HttpStatus.BAD_REQUEST,message,request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,HttpServletRequest request){
        return buildError(HttpStatus.BAD_REQUEST,ex.getMessage(),request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex,HttpServletRequest request){
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Server Error",request);
    }
}
