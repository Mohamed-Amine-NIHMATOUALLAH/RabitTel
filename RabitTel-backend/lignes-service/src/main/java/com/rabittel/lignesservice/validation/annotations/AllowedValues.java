package com.rabittel.lignesservice.validation.annotations;

import com.rabittel.lignesservice.validation.validators.AllowedValuesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = AllowedValuesValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface AllowedValues {
    String message() default "contains an invalid value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();
}
