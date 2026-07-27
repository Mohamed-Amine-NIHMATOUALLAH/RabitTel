package com.rabittel.lignesservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDTO {

    private UUID id;

    private String name;

    private BigDecimal price;

    private String description;

    private Boolean active;

    private LocalDateTime creationDate;

    /**
     * Total number of telecom lines associated with this plan.
     */
    private Long linesCount;
}
