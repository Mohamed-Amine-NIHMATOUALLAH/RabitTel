package com.rabittel.lignesservice.mappers;

import com.rabittel.lignesservice.dtos.response.LineResponseDTO;
import com.rabittel.lignesservice.entities.Line;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LineMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.endDate", target = "contractEndDate")
    LineResponseDTO toLineResponseDTO(Line line);
}
