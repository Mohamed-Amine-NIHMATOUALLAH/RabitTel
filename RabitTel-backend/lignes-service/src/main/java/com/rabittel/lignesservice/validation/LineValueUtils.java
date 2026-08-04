package com.rabittel.lignesservice.validation;

import java.util.Locale;
import java.util.regex.Pattern;

public final class LineValueUtils {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$"
    );
    private static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    private LineValueUtils() {
    }

    public static String normalizeMoroccanPhoneNumber(String value, char prefix) {
        if (value == null) {
            throw new IllegalArgumentException("Phone number is required");
        }

        String cleaned = value.trim().replaceAll("[\\s\\-\\.()]", "");
        if (cleaned.startsWith("+212")) {
            cleaned = cleaned.substring(4);
        }

        if (cleaned.startsWith("212")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.length() == 10 && cleaned.charAt(0) == '0' && cleaned.charAt(1) == prefix) {
            cleaned = cleaned.substring(1);
        }

        if (cleaned.length() == 9 && cleaned.charAt(0) == prefix && DIGITS_PATTERN.matcher(cleaned).matches()) {
            return cleaned;
        }

        throw new IllegalArgumentException("Invalid Moroccan phone number");
    }

    public static String formatMoroccanPhoneNumber(String normalizedValue) {
        if (normalizedValue == null || normalizedValue.isBlank()) {
            return normalizedValue;
        }

        if (normalizedValue.length() == 9 && DIGITS_PATTERN.matcher(normalizedValue).matches()) {
            char prefix = normalizedValue.charAt(0);
            if (prefix == '5' || prefix == '6') {
                return "+212 " + normalizedValue;
            }
        }

        return normalizedValue;
    }

    public static boolean isDigitsOnly(String value) {
        return value != null && DIGITS_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphanumeric(String value) {
        return value != null && ALPHANUMERIC_PATTERN.matcher(value).matches();
    }

    public static boolean isIpv4(String value) {
        return value != null && IPV4_PATTERN.matcher(value).matches();
    }

    public static boolean isAllowedValue(String value, String[] allowedValues) {
        if (value == null || allowedValues == null) {
            return false;
        }
        for (String allowed : allowedValues) {
            if (allowed != null && allowed.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
