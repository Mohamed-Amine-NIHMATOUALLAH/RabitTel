package com.rabittel.lignesservice.enums;

public enum LineStatus {

    ACTIVE("ACTIF"),
    SUSPENDED("SUSPENDU"),
    TERMINATED("RÉSILIÉ"),
    TRANSFERRED("CESSION");

    private final String label;

    LineStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}