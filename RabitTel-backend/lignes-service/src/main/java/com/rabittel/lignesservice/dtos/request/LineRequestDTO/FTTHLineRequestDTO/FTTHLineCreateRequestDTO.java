package com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO;

import com.rabittel.lignesservice.enums.FTTHBandwidth;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
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
public class FTTHLineCreateRequestDTO {

    @NotBlank
    @MoroccanPhoneNumber(prefix = '5')
    private String lineNumber;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal contractualAmount;

    @NotNull
    private UUID agencyId;

    @NotBlank
    @MoroccanPhoneNumber(prefix = '5')
    private String fixedLineNumber;

    @NotBlank
    private String routerBrand;

    @NotNull
    private FTTHBandwidth bandwidth;
}
