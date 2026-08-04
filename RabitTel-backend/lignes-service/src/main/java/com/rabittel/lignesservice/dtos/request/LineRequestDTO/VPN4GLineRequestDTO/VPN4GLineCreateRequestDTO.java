package com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO;
import com.rabittel.lignesservice.validation.annotations.Alphanumeric;
import com.rabittel.lignesservice.validation.annotations.Ipv4;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VPN4GLineCreateRequestDTO {

    @NotBlank
    @MoroccanPhoneNumber(prefix = '6')
    private String lineNumber;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal contractualAmount;

    @NotNull
    private UUID agencyId;

    @NotBlank
    @Alphanumeric
    private String equipment;

    @NotBlank
    @Ipv4
    private String ipAddress;

    @NotBlank
    @Alphanumeric
    private String serialNumber;

    @NotNull
    private LocalDate deliveryDate;
}
