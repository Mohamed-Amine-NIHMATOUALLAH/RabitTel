package com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO;
import com.rabittel.lignesservice.validation.annotations.Ipv4;
import com.rabittel.lignesservice.enums.LineType;
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
public class DataLineCreateRequestDTO {

    @NotBlank
    private String lineNumber;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal contractualAmount;

    @NotNull
    private UUID agencyId;

    @NotNull
    private String bandwidth;

    @NotBlank
    @Ipv4
    private String ipAddress;

    @NotNull
    private LineType lineType;
}