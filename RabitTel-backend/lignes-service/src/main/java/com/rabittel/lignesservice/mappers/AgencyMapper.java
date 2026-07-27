package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.AgencyResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AgencyMapper {

    // Entity -> Response DTO
    @Mapping(target = "linesCount", expression = "java(agency.getLines() == null ? 0L : (long) agency.getLines().size())")
    AgencyResponseDTO toAgencyResponseDTO(Agency agency);

    // Create DTO -> Entity
    Agency toEntity(AgencyCreateRequestDTO agencyCreateRequestDTO);

    // Update DTO -> Entity
    void updateEntityFromDto(AgencyUpdateRequestDTO agencyUpdateRequestDTO, @MappingTarget Agency agency);
}