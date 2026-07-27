package com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO;

import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VPN4GLineUpdateRequestDTO {

    // Base fields
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID planId;
    private UUID contractId;
    private UUID createdBy;

    // VPN4G specific
    private String equipment;
    private String ipAddress;
    private String serialNumber;
}