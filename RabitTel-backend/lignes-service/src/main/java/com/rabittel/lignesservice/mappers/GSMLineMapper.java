package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.GSMLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.GSMLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.GSMLineResponseDTO;
import com.rabittel.lignesservice.entities.GSMLine;
import com.rabittel.lignesservice.validation.LineValueUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GSMLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    GSMLineResponseDTO toGSMLineResponseDTO(GSMLine gsmLine);

    @Mapping(source = "agencyId", target = "agency.id")
    @Mapping(source = "planId", target = "plan.id")
    GSMLine toEntity(GSMLineCreateRequestDTO gsmLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(GSMLineUpdateRequestDTO gsmLineUpdateRequestDTO, @MappingTarget GSMLine gsmLine);

    @AfterMapping
    default void formatResponse(@MappingTarget GSMLineResponseDTO dto) {
        dto.setLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getLineNumber()));
    }
}
