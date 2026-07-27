package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.PlanResponseDTO;
import com.rabittel.lignesservice.entities.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    // Entity -> Response DTO
    @Mapping(target = "linesCount", expression = "java(plan.getLines() != null ? plan.getLines().size() : 0L)")
    PlanResponseDTO toPlanResponseDTO(Plan plan);

    // Create DTO -> Entity
    Plan toEntity(PlanCreateRequestDTO planCreateRequestDTO);

    // Update DTO -> Entity
    void updatePlanFromRequestDTO(PlanUpdateRequestDTO planUpdateRequestDTO, @MappingTarget Plan plan);

}
