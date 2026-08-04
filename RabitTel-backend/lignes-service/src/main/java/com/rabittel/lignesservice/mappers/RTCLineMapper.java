package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.RTCLineResponseDTO;
import com.rabittel.lignesservice.entities.RTCLine;
import com.rabittel.lignesservice.validation.LineValueUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RTCLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    RTCLineResponseDTO toRTCLineResponseDTO(RTCLine rtcLine);

    @Mapping(source = "agencyId", target = "agency.id")
    RTCLine toEntity(RTCLineCreateRequestDTO rtcLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(RTCLineUpdateRequestDTO rtcLineUpdateRequestDTO, @MappingTarget RTCLine rtcLine);

    @AfterMapping
    default void formatResponse(@MappingTarget RTCLineResponseDTO dto) {
        dto.setLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getLineNumber()));
    }
}
