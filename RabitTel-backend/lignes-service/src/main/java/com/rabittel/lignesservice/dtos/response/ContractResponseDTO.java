package com.rabittel.lignesservice.dtos.response;

import com.rabittel.lignesservice.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {

    private UUID id;

    private LocalDate startDate;

    private Integer durationMonths;

    private LocalDate endDate;

    private ContractStatus status;

    private LocalDateTime creationDate;

    /**
     * Total number of telecom lines associated with this contract.
     */
    private Long linesCount;
}
