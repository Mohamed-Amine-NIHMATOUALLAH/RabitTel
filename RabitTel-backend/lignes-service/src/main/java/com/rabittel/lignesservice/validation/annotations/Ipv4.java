package com.rabittel.lignesservice.validation.annotations;

import com.rabittel.lignesservice.validation.validators.Ipv4Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = Ipv4Validator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Ipv4 {
    String message() default "must be a valid IPv4 address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
