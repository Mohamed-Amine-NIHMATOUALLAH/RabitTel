package com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO;

import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VPNLineUpdateRequestDTO {

    // Base fields
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID planId;
    private UUID contractId;
    private UUID createdBy;

    // VPN specific
    private String bandwidth;
    private String ipAddress;

    private LocalDate deliveryDate;
}