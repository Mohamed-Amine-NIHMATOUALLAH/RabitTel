package com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO;

import com.rabittel.lignesservice.validation.annotations.DigitsOnly;
import com.rabittel.lignesservice.validation.annotations.MoroccanPhoneNumber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class GSMLineCreateRequestDTO {

    @NotBlank
    @MoroccanPhoneNumber(prefix = '6')
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
    @DigitsOnly
    private String chipSerialNumber;

    @NotNull
    private LocalDate chipDeliveryDate;

    @NotBlank
    @DigitsOnly
    @Size(min = 4, max = 4)
    private String pinCode;

    @NotBlank
    @DigitsOnly
    @Size(min = 8, max = 8)
    private String pukCode;
}
