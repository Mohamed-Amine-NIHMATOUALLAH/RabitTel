package com.rabittel.lignesservice.mappers;


import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractRenewalRequestDTO;
import com.rabittel.lignesservice.dtos.response.ContractResponseDTO;
import com.rabittel.lignesservice.entities.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractMapper {
    // Entity -> Response DTO (linesCount must be set by the service)
    ContractResponseDTO toContractResponseDTO(Contract contract);

    // Create DTO -> Entity
    Contract toEntity(ContractCreateRequestDTO contractCreateRequestDTO);

    // Update DTO -> Entity
    void updateContractFromResponseDTO(ContractRenewalRequestDTO contractRenewalRequestDTO, @MappingTarget Contract contract);

}