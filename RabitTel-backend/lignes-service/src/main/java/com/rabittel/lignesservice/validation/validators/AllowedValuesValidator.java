package com.rabittel.lignesservice.validation.validators;

import com.rabittel.lignesservice.validation.LineValueUtils;
import com.rabittel.lignesservice.validation.annotations.AllowedValues;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    private String[] values;

    @Override
    public void initialize(AllowedValues constraintAnnotation) {
        this.values = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || LineValueUtils.isAllowedValue(value, values);
    }
}
