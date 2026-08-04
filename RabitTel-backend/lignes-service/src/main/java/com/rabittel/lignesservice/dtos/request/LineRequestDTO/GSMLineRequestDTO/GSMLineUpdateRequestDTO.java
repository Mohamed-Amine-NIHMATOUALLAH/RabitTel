package com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO;

import com.rabittel.lignesservice.validation.annotations.DigitsOnly;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import jakarta.validation.constraints.Size;
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
    @MoroccanPhoneNumber(prefix = '6')
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID planId;
    private UUID contractId;

    // GSM specific
    private String serviceFunction;
    @DigitsOnly
    private String chipSerialNumber;
    private LocalDate chipDeliveryDate;
    @DigitsOnly
    @Size(min = 4, max = 4)
    private String pinCode;
    @DigitsOnly
    @Size(min = 8, max = 8)
    private String pukCode;
}
