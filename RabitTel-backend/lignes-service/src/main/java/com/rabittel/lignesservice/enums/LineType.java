package com.rabittel.lignesservice.enums;

public enum LineType {

    FTTH("FTTH"),
    RTC("RTC"),
    VPN_ADSL("VPN ADSL"),
    G4("4G"),
    G4_VPN("4G VPN"),
    GSM_PRO("GSM Pro");

    private final String label;

    LineType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
