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
public class VPN4GLineResponseDTO extends LineResponseDTO {
    private String equipment;
    private String ipAddress;
    private String serialNumber;
    private LocalDate deliveryDate;
}