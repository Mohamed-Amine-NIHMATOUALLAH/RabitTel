package com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO;

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
public class GSMLineUpdateRequestDTO {

    // Base line fields (optional to allow partial updates)
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID planId;
    private UUID contractId;

    // GSM specific
    private String serviceFunction;
    private String chipSerialNumber;
    private LocalDate chipDeliveryDate;
    private String pinCode;
    private String pukCode;
}
