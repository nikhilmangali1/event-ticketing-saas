package com.nikhil.ticketflow.common.exceptions;

import com.nikhil.ticketflow.common.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest request){
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField()+": "+error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(
                        400,
                        "VALIDATION_FAILED",
                        "validation failed",
                        request.getRequestURI(),
                        details
                )
        );
    }


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationException(ApplicationException ex,
                                                                       HttpServletRequest request){
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(
                        ex.getHttpStatus().value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex,
                                                                 HttpServletRequest request){
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(
                        403,
                        "ACCESS_DENIED",
                        "You are not authorized to perform this action",
                        request.getRequestURI()
                )
        );
    }

    public ResponseEntity<ApiErrorResponse> handleException(Exception ex,
                                                            HttpServletRequest request){
        log.error("Unhandled exception occurred", ex);
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "Something went wrong",
                        request.getRequestURI()
                )
        );
    }
}
