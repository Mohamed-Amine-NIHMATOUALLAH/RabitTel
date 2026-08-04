package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.Internet4GLineResponseDTO;
import com.rabittel.lignesservice.entities.Internet4GLine;
import com.rabittel.lignesservice.enums.Internet4GBandwidth;
import com.rabittel.lignesservice.validation.LineValueUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", imports = {Internet4GBandwidth.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Internet4GLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    @Mapping(target = "bandwidth", expression = "java(internet4GLine.getBandwidth() != null ? internet4GLine.getBandwidth().getLabel() : null)")
    Internet4GLineResponseDTO toInternet4GLineResponseDTO(Internet4GLine internet4GLine);

    @Mapping(source = "agencyId", target = "agency.id")
    Internet4GLine toEntity(Internet4GLineCreateRequestDTO internet4GLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(Internet4GLineUpdateRequestDTO internet4GLineUpdateRequestDTO, @MappingTarget Internet4GLine internet4GLine);

    @AfterMapping
    default void formatResponse(@MappingTarget Internet4GLineResponseDTO dto) {
        dto.setLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getLineNumber()));
    }
}
