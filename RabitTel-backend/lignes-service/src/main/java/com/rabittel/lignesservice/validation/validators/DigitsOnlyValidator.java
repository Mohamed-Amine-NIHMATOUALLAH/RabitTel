package com.rabittel.lignesservice.validation.validators;

import com.rabittel.lignesservice.validation.LineValueUtils;
import com.rabittel.lignesservice.validation.annotations.DigitsOnly;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DigitsOnlyValidator implements ConstraintValidator<DigitsOnly, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || LineValueUtils.isDigitsOnly(value);
    }
}
