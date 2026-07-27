package com.rabittel.lignesservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RTCLineResponseDTO extends LineResponseDTO {
    // pas de champ spécifique — hérite uniquement des champs communs

    // explicit no-arg constructor to avoid Lombok duplicate-generation issues
    public RTCLineResponseDTO() {
        super();
    }
}
