package com.rabittel.lignesservice.dtos.request.AgencyRequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyUpdateRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String directorateCode;

    @NotBlank
    private String region;

    @NotNull
    private Boolean active;
}