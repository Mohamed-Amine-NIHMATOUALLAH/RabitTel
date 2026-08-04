package com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO;

import com.rabittel.lignesservice.validation.annotations.Ipv4;
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
public class DataLineUpdateRequestDTO {

    // Base fields
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID contractId;

    // VPN specific
    private String bandwidth;
    @Ipv4
    private String ipAddress;

}