package com.rabittel.lignesservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgencyResponseDTO {

    private UUID id;

    private String name;

    private String directorateCode;

    private String region;

    private Boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime lastModificationDate;

    //  Total number of telecom lines assigned to this agency
    private Long linesCount;
}