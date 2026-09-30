package com.daniel.identity_service.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
    String message() default "Invalid Date of birth";

    int min();

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
