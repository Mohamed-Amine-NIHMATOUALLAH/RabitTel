package com.rabittel.lignesservice.dtos.request.ContractRequestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractCreateRequestDTO {

    @NotNull
    private LocalDate startDate;

    @NotNull
    @Min(1)
    private Integer durationMonths;
}