package com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO;
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
    private String lineNumber;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal contractualAmount;

    @NotNull
    private UUID agencyId;

    @NotNull
    private UUID planId;

    @NotBlank
    private String equipment;

    @NotBlank
    private String ipAddress;

    @NotBlank
    private String serialNumber;

    @NotNull
    private LocalDate deliveryDate;
}
