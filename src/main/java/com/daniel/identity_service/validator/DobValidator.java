package com.daniel.identity_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int min;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        long yearsBetween = ChronoUnit.YEARS.between(value, LocalDate.now());

        return yearsBetween >= min;
    }
}
