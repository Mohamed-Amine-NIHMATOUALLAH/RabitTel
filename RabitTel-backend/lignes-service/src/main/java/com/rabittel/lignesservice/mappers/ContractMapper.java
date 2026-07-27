package com.rabittel.lignesservice.mappers;


import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractRenewalRequestDTO;
import com.rabittel.lignesservice.dtos.response.ContractResponseDTO;
import com.rabittel.lignesservice.entities.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    // Entity -> Response DTO
    @Mapping(target = "linesCount", expression = "java(contract.getLines() == null ? 0L : (long) contract.getLines().size())")
    ContractResponseDTO toContractResponseDTO(Contract contract);

    // Create DTO -> Entity
    Contract toEntity(ContractCreateRequestDTO contractCreateRequestDTO);

    // Update DTO -> Entity
    void updateContractFromResponseDTO(ContractRenewalRequestDTO contractRenewalRequestDTO, @MappingTarget Contract contract);

}