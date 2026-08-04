package com.rabittel.lignesservice.validation.validators;

import com.rabittel.lignesservice.validation.LineValueUtils;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MoroccanPhoneNumberValidator implements ConstraintValidator<MoroccanPhoneNumber, String> {

    private char prefix;

    @Override
    public void initialize(MoroccanPhoneNumber constraintAnnotation) {
        this.prefix = constraintAnnotation.prefix();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            LineValueUtils.normalizeMoroccanPhoneNumber(value, prefix);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
