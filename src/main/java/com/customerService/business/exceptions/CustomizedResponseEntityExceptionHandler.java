package com.customerService.business.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                errors.toString());
        return ResponseEntity.status(status).body(errorResponse);
    }

//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public ResponseEntity<Object> handleSQLIntegrityConstraintViolation(WebRequest request) {
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        ErrorResponse errorResponse = new ErrorResponse(
//                LocalDateTime.now(),
//                status.value(),
//                status.getReasonPhrase(),
//                "Constraint Violation, make sure to insert existing data",
//                request.getDescription(false));
//        return ResponseEntity.status(status).body(errorResponse);
//    }
}
