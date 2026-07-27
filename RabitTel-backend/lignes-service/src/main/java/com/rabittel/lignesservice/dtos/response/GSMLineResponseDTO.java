package com.rabittel.lignesservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GSMLineResponseDTO extends LineResponseDTO {
    private String serviceFunction;
    private String chipSerialNumber;
    private LocalDate chipDeliveryDate;
    private String pinCode;
    private String pukCode;
}