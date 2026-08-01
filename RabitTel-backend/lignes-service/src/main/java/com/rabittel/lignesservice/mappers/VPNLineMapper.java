package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.VPNLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.VPNLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.VPNLineResponseDTO;
import com.rabittel.lignesservice.entities.VPNLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VPNLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    VPNLineResponseDTO toVPNLineResponseDTO(VPNLine vpnLine);

    @Mapping(source = "agencyId", target = "agency.id")
    @Mapping(source = "planId", target = "plan.id")
    VPNLine toEntity(VPNLineCreateRequestDTO vpnLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VPNLineUpdateRequestDTO vpnLineUpdateRequestDTO, @MappingTarget VPNLine vpnLine);
}
