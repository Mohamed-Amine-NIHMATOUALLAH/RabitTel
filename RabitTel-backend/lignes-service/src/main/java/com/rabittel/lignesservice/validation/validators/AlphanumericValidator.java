package com.rabittel.lignesservice.validation.validators;

import com.rabittel.lignesservice.validation.LineValueUtils;
import com.rabittel.lignesservice.validation.annotations.Alphanumeric;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AlphanumericValidator implements ConstraintValidator<Alphanumeric, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || LineValueUtils.isAlphanumeric(value);
    }
}
