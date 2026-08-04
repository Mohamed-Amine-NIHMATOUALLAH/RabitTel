package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.PlanResponseDTO;
import com.rabittel.lignesservice.entities.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlanMapper {

    // Entity -> Response DTO (linesCount must be set by the service)
    PlanResponseDTO toPlanResponseDTO(Plan plan);

    // Create DTO -> Entity
    Plan toEntity(PlanCreateRequestDTO planCreateRequestDTO);

    // Update DTO -> Entity
    void updatePlanFromRequestDTO(PlanUpdateRequestDTO planUpdateRequestDTO, @MappingTarget Plan plan);

}
