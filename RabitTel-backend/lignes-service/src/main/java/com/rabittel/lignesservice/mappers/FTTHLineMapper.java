package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.FTTHLineResponseDTO;
import com.rabittel.lignesservice.entities.FTTHLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FTTHLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    FTTHLineResponseDTO toFTTHLineResponseDTO(FTTHLine ftthLine);

    FTTHLine toEntity(FTTHLineCreateRequestDTO ftthLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FTTHLineUpdateRequestDTO ftthLineUpdateRequestDTO, @MappingTarget FTTHLine ftthLine);
}
