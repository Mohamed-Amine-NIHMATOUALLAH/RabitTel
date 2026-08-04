package com.rabittel.lignesservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

public enum Internet4GBandwidth {
    GO_40("40Go"),
    GO_70("70Go"),
    GO_90("90Go");

    private final String label;

    Internet4GBandwidth(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Internet4GBandwidth fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(bw -> bw.label.equalsIgnoreCase(value.trim()) || bw.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Internet4G bandwidth value: " + value));
    }

    @Converter(autoApply = false)
    public static class Internet4GBandwidthConverter implements AttributeConverter<Internet4GBandwidth, String> {
        @Override
        public String convertToDatabaseColumn(Internet4GBandwidth attribute) {
            return attribute == null ? null : attribute.getLabel();
        }

        @Override
        public Internet4GBandwidth convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Internet4GBandwidth.fromValue(dbData);
        }
    }
}
