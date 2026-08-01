package com.rabittel.lignesservice.dtos.response;

import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class LineResponseDTO {

    private UUID id;
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;

    private UUID agencyId;
    private String agencyName;

    private UUID planId;
    private String planName;

    private UUID contractId;
    private LocalDate contractEndDate;


    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
}