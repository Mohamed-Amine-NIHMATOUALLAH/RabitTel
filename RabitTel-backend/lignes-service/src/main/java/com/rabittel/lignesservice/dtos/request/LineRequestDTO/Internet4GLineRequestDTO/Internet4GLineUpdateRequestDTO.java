package com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO;

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
public class Internet4GLineUpdateRequestDTO {

    // Base fields
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID planId;
    private UUID contractId;
    private UUID createdBy;

    // Internet4G specific
    private String serviceFunction;
    private String simSerialNumber;
    private String pinCode;
    private String pukCode;
    private String equipment;
    private String equipmentSerialNumber;
    private String bandwidth;
}