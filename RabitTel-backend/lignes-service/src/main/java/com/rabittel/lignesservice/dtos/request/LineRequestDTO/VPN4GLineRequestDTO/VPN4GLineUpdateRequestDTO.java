package com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO;

import com.rabittel.lignesservice.validation.annotations.Alphanumeric;
import com.rabittel.lignesservice.validation.annotations.Ipv4;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
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
public class VPN4GLineUpdateRequestDTO {

    // Base fields
    @MoroccanPhoneNumber(prefix = '6')
    private String lineNumber;
    private LineType lineType;
    private LineStatus lineStatus;
    private BigDecimal contractualAmount;
    private UUID agencyId;
    private UUID contractId;

    // VPN4G specific
    @Alphanumeric
    private String equipment;
    @Ipv4
    private String ipAddress;
    @Alphanumeric
    private String serialNumber;
    private LocalDate deliveryDate;
}