package com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO;
import com.rabittel.lignesservice.enums.Internet4GBandwidth;
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
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Internet4GLineCreateRequestDTO {

    @NotBlank
    @MoroccanPhoneNumber(prefix = '6')
    private String lineNumber;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal contractualAmount;

    @NotNull
    private UUID agencyId;

    @NotBlank
    private String serviceFunction;

    @NotBlank
    @DigitsOnly
    private String simSerialNumber;

    @NotBlank
    @DigitsOnly
    @Size(min = 4, max = 4)
    private String pinCode;

    @NotBlank
    @DigitsOnly
    @Size(min = 8, max = 8)
    private String pukCode;

    @NotBlank
    private String equipment;

    @NotBlank
    private String equipmentSerialNumber;

    @NotNull
    private Internet4GBandwidth bandwidth;
}
