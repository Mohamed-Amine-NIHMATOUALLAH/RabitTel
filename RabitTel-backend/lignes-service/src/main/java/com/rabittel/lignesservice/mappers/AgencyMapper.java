package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.AgencyResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgencyMapper {

    // Entity -> Response DTO (linesCount must be set by the service)
    AgencyResponseDTO toAgencyResponseDTO(Agency agency);

    // Create DTO -> Entity
    Agency toEntity(AgencyCreateRequestDTO agencyCreateRequestDTO);

    // Update DTO -> Entity
    void updateEntityFromDto(AgencyUpdateRequestDTO agencyUpdateRequestDTO, @MappingTarget Agency agency);
}