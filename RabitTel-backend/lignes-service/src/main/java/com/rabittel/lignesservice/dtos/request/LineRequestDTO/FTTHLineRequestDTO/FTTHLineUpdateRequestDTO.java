package com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO;

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
public class FTTHLineUpdateRequestDTO {

    // Base fields
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID planId;
    private UUID contractId;

    // FTTH specific
    private String fixedLineNumber;
    private String routerBrand;
    private Long bandwidth;
}