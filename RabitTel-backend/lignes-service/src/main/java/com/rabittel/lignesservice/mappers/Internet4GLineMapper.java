package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.Internet4GLineResponseDTO;
import com.rabittel.lignesservice.entities.Internet4GLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface Internet4GLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    Internet4GLineResponseDTO toInternet4GLineResponseDTO(Internet4GLine internet4GLine);

    @Mapping(source = "agencyId", target = "agency.id")
    @Mapping(source = "planId", target = "plan.id")
    Internet4GLine toEntity(Internet4GLineCreateRequestDTO internet4GLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(Internet4GLineUpdateRequestDTO internet4GLineUpdateRequestDTO, @MappingTarget Internet4GLine internet4GLine);
}
