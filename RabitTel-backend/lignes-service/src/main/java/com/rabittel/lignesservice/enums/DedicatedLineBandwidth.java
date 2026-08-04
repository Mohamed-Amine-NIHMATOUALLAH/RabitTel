package com.rabittel.lignesservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum DedicatedLineBandwidth {
    M_8("8M"),
    M_10("10M"),
    M_20("20M"),
    M_50("50M");

    private final String label;

    DedicatedLineBandwidth(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static DedicatedLineBandwidth fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(bw -> bw.label.equalsIgnoreCase(value.trim()) || bw.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid dedicated line bandwidth value: " + value));
    }
}
