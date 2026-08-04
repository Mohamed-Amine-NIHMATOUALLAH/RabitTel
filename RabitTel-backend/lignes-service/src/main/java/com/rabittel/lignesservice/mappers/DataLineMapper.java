package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.DataLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.DataLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.DataLineResponseDTO;
import com.rabittel.lignesservice.entities.DataLine;
import com.rabittel.lignesservice.validation.LineValueUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    DataLineResponseDTO toDataLineResponseDTO(DataLine dataLine);

    @Mapping(source = "agencyId", target = "agency.id")
    DataLine toEntity(DataLineCreateRequestDTO dataLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(DataLineUpdateRequestDTO dataLineUpdateRequestDTO, @MappingTarget DataLine dataLine);

    @AfterMapping
    default void formatResponse(@MappingTarget DataLineResponseDTO dto) {
        dto.setLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getLineNumber()));
    }
}
