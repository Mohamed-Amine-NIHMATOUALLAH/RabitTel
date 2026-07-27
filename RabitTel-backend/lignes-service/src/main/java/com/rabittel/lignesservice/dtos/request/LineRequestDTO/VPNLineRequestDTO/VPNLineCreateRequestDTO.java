package com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VPNLineCreateRequestDTO {

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
    private String bandwidth;

    @NotBlank
    private String ipAddress;
}