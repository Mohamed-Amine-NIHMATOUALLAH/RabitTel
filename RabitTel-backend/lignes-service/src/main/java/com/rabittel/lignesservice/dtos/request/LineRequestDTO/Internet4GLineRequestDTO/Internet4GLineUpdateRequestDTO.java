package com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO;

import com.rabittel.lignesservice.enums.Internet4GBandwidth;
import com.rabittel.lignesservice.validation.annotations.DigitsOnly;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import jakarta.validation.constraints.Size;
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
    @MoroccanPhoneNumber(prefix = '6')
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID contractId;

    // Internet4G specific
    private String serviceFunction;
    @DigitsOnly
    private String simSerialNumber;
    @DigitsOnly
    @Size(min = 4, max = 4)
    private String pinCode;
    @DigitsOnly
    @Size(min = 8, max = 8)
    private String pukCode;
    private String equipment;
    private String equipmentSerialNumber;
    private Internet4GBandwidth bandwidth;
}
