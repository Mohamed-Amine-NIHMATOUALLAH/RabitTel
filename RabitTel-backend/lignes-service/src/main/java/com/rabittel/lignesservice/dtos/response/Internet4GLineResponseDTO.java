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
public class Internet4GLineResponseDTO extends LineResponseDTO {
    private String serviceFunction;
    private String simSerialNumber;
    private String pinCode;
    private String pukCode;
    private String equipment;
    private String equipmentSerialNumber;
    private Long bandwidth;
}