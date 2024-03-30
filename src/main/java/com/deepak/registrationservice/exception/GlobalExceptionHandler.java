package com.deepak.registrationservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<Object> handleDuplicateEntryException(DuplicateEntryException ex) {
        LOGGER.error("Duplicate entry error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = ErrorDetails.builder().details(ex.getMessage()).message("Duplicate Entry").timestamp(String.valueOf(LocalDateTime.now())).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Object> handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        LOGGER.error("AppointmentNot Found error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = ErrorDetails.builder().details(ex.getMessage()).message("Not Found").timestamp(String.valueOf(LocalDateTime.now())).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);

    }


    @ExceptionHandler(SlotIdNotAvailableException.class)
    public ResponseEntity<Object> handleSlotIdNotAvailableException(SlotIdNotAvailableException ex) {
        LOGGER.error("Slot not available error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = ErrorDetails.builder().details(ex.getMessage()).message("Slot not available").timestamp(String.valueOf(LocalDateTime.now())).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(String.valueOf(LocalDateTime.now()), "User not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder().details(ex.getMessage()).message("Input Validation Failed").timestamp(String.valueOf(LocalDateTime.now())).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage());
        ex.printStackTrace();
        ErrorDetails errorDetails = ErrorDetails.builder().details(ex.getMessage()).timestamp(String.valueOf(LocalDateTime.now())).details(ex.getLocalizedMessage()).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
