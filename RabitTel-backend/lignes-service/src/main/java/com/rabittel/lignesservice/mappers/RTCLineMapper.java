package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.RTCLineResponseDTO;
import com.rabittel.lignesservice.entities.RTCLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RTCLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    RTCLineResponseDTO toRTCLineResponseDTO(RTCLine rtcLine);

    RTCLine toEntity(RTCLineCreateRequestDTO rtcLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(RTCLineUpdateRequestDTO rtcLineUpdateRequestDTO, @MappingTarget RTCLine rtcLine);
}
