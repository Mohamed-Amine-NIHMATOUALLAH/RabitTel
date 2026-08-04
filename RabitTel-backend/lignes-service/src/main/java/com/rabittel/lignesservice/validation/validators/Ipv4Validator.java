package com.rabittel.lignesservice.validation.validators;

import com.rabittel.lignesservice.validation.LineValueUtils;
import com.rabittel.lignesservice.validation.annotations.Ipv4;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Ipv4Validator implements ConstraintValidator<Ipv4, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || LineValueUtils.isIpv4(value);
    }
}
