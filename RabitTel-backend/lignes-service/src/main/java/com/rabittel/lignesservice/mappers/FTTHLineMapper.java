package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.FTTHLineResponseDTO;
import com.rabittel.lignesservice.entities.FTTHLine;
import com.rabittel.lignesservice.enums.FTTHBandwidth;
import com.rabittel.lignesservice.validation.LineValueUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", imports = {FTTHBandwidth.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FTTHLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    @Mapping(target = "bandwidth", expression = "java(ftthLine.getBandwidth() != null ? ftthLine.getBandwidth().getLabel() : null)")
    FTTHLineResponseDTO toFTTHLineResponseDTO(FTTHLine ftthLine);

    @Mapping(source = "agencyId", target = "agency.id")
    FTTHLine toEntity(FTTHLineCreateRequestDTO ftthLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FTTHLineUpdateRequestDTO ftthLineUpdateRequestDTO, @MappingTarget FTTHLine ftthLine);

    @AfterMapping
    default void formatResponse(@MappingTarget FTTHLineResponseDTO dto) {
        dto.setLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getLineNumber()));
        dto.setFixedLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getFixedLineNumber()));
    }
}
