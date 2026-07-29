package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractRenewalRequestDTO;
import com.rabittel.lignesservice.dtos.response.ContractResponseDTO;
import com.rabittel.lignesservice.enums.ContractStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ContractService {

    ContractResponseDTO createContract(ContractCreateRequestDTO dto);

    ContractResponseDTO renewContract(UUID id, ContractRenewalRequestDTO renewalRequestDTO);

    void deleteContract(UUID id);

    List<ContractResponseDTO> getActiveContracts();

    List<ContractResponseDTO> getExpiredContracts();

    ContractResponseDTO getContractById(UUID id);

    List<ContractResponseDTO> getAllContracts();

    List<ContractResponseDTO> getContractsByStatus(ContractStatus status);

    List<ContractResponseDTO> getExpiringContracts(int daysThreshold);

    Long getDaysUntilExpiration(UUID id);

    List<ContractResponseDTO> getContractsByDateRange(LocalDate startDate, LocalDate endDate);

    List<ContractResponseDTO> searchContracts(ContractStatus status, LocalDate startDateFrom, LocalDate startDateTo,
                                              LocalDate endDateFrom, LocalDate endDateTo);
}