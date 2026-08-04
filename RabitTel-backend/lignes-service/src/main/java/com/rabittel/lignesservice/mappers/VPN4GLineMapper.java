package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.VPN4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.VPN4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.VPN4GLineResponseDTO;
import com.rabittel.lignesservice.entities.VPN4GLine;
import com.rabittel.lignesservice.validation.LineValueUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VPN4GLineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    VPN4GLineResponseDTO toVPN4GLineResponseDTO(VPN4GLine vpn4GLine);

    @Mapping(source = "agencyId", target = "agency.id")
    VPN4GLine toEntity(VPN4GLineCreateRequestDTO vpn4GLineCreateRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VPN4GLineUpdateRequestDTO vpn4GLineUpdateRequestDTO, @MappingTarget VPN4GLine vpn4GLine);

    @AfterMapping
    default void formatResponse(@MappingTarget VPN4GLineResponseDTO dto) {
        dto.setLineNumber(LineValueUtils.formatMoroccanPhoneNumber(dto.getLineNumber()));
    }
}
