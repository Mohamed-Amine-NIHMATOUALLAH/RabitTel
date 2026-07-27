package com.rabittel.lignesservice.enums;

public enum ContractStatus {

    IN_PROGRESS("En cours"),
    EXPIRED("Expiré"),
    RENEWED("Renouvelé");

    private final String label;

    ContractStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
