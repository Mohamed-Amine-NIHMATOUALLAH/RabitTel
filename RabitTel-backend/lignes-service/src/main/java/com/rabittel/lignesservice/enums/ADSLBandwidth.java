package com.rabittel.lignesservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ADSLBandwidth {
    M_4("4M"),
    M_12("12M"),
    M_20("20M");

    private final String label;

    ADSLBandwidth(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ADSLBandwidth fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(bw -> bw.label.equalsIgnoreCase(value.trim()) || bw.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid ADSL bandwidth value: " + value));
    }
}
