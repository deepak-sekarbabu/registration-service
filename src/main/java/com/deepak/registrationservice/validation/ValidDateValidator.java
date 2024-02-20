package com.deepak.registrationservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

class ValidDateValidator implements ConstraintValidator<ValidDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are validated separately if needed
        }
        try {
            // Try parsing the LocalDate using the standard format
            LocalDate.parse(value.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}