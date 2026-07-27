package com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO;

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
public class GSMLineCreateRequestDTO {

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
    private String serviceFunction;

    @NotBlank
    private String chipSerialNumber;

    @NotNull
    private LocalDate chipDeliveryDate;

    @NotBlank
    private String pinCode;

    @NotBlank
    private String pukCode;
}