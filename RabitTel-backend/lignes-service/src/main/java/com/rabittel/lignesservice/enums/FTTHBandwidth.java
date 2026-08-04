package com.rabittel.lignesservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

public enum FTTHBandwidth {
    GO_1("1Go"),
    MO_200("200Mo"),
    MO_100("100Mo");

    private final String label;

    FTTHBandwidth(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static FTTHBandwidth fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(bw -> bw.label.equalsIgnoreCase(value.trim()) || bw.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid FTTH bandwidth value: " + value));
    }

    @Converter(autoApply = false)
    public static class FTTHBandwidthConverter implements AttributeConverter<FTTHBandwidth, String> {
        @Override
        public String convertToDatabaseColumn(FTTHBandwidth attribute) {
            return attribute == null ? null : attribute.getLabel();
        }

        @Override
        public FTTHBandwidth convertToEntityAttribute(String dbData) {
            return dbData == null ? null : FTTHBandwidth.fromValue(dbData);
        }
    }
}
