package com.rabittel.lignesservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FTTHLineResponseDTO extends LineResponseDTO {
    private String fixedLineNumber;
    private String routerBrand;
    private Long bandwidth;
}