package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractRenewalRequestDTO;
import com.rabittel.lignesservice.dtos.response.ContractResponseDTO;
import com.rabittel.lignesservice.entities.Contract;
import com.rabittel.lignesservice.enums.ContractStatus;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.ContractMapper;
import com.rabittel.lignesservice.repositories.ContractRepository;
import com.rabittel.lignesservice.specifications.ContractSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    public ContractResponseDTO createContract(ContractCreateRequestDTO contractCreateRequestDTO) {
        if (contractCreateRequestDTO.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        if (contractCreateRequestDTO.getDurationMonths() == null || contractCreateRequestDTO.getDurationMonths() < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        Contract contract = contractMapper.toEntity(contractCreateRequestDTO);
        LocalDate endDate = contract.getStartDate().plusMonths(contract.getDurationMonths());
        contract.setEndDate(endDate);
        contract.setStatus(ContractStatus.IN_PROGRESS);

        Contract savedContract = contractRepository.save(contract);
        return contractMapper.toContractResponseDTO(savedContract);
    }

    public ContractResponseDTO renewContract(UUID id, ContractRenewalRequestDTO renewalRequestDTO) {
        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + id + " not found."));

        if (renewalRequestDTO.getNewDurationMonths() == null || renewalRequestDTO.getNewDurationMonths() < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        contract.setDurationMonths(renewalRequestDTO.getNewDurationMonths());
        LocalDate newEndDate = contract.getStartDate().plusMonths(renewalRequestDTO.getNewDurationMonths());
        contract.setEndDate(newEndDate);

        if (contract.getEndDate().isBefore(LocalDate.now())) {
            contract.setStatus(ContractStatus.EXPIRED);
        } else {
            contract.setStatus(ContractStatus.RENEWED);
        }

        Contract updatedContract = contractRepository.save(contract);
        return contractMapper.toContractResponseDTO(updatedContract);
    }

    public void deleteContract(UUID id) {
        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + id + " not found."));

        if (contract.getLines() != null && !contract.getLines().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete contract with associated lines - delete or transfer lines first");
        }

        contractRepository.delete(contract);
    }

    public List<ContractResponseDTO> getAllContracts() {
        return contractRepository.findAll().stream()
            .map(contractMapper::toContractResponseDTO)
            .collect(Collectors.toList());
    }

    public ContractResponseDTO getContractById(UUID id) {
        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + id + " not found."));
        return contractMapper.toContractResponseDTO(contract);
    }

    public List<ContractResponseDTO> getContractsByStatus(ContractStatus status) {
        return contractRepository.findByStatus(status).stream()
            .map(contractMapper::toContractResponseDTO)
            .collect(Collectors.toList());
    }

    public List<ContractResponseDTO> getActiveContracts() {
        List<ContractResponseDTO> activeContracts = getContractsByStatus(ContractStatus.IN_PROGRESS);
        activeContracts.addAll(getContractsByStatus(ContractStatus.RENEWED));
        return activeContracts;
    }

    public List<ContractResponseDTO> getExpiredContracts() {
        return getContractsByStatus(ContractStatus.EXPIRED);
    }

    public List<ContractResponseDTO> getExpiringContracts(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return contractRepository.findContractsExpiringBefore(thresholdDate).stream()
            .map(contractMapper::toContractResponseDTO)
            .collect(Collectors.toList());
    }

    public List<ContractResponseDTO> getContractsByDateRange(LocalDate startDate, LocalDate endDate) {
        return contractRepository.findContractsByDateRange(startDate, endDate).stream()
            .map(contractMapper::toContractResponseDTO)
            .collect(Collectors.toList());
    }

    public List<ContractResponseDTO> searchContracts(ContractStatus status, LocalDate startDateFrom, LocalDate startDateTo,
                                                     LocalDate endDateFrom, LocalDate endDateTo) {
        Specification<Contract> spec = Specification
            .<Contract>where(ContractSpecification.hasStatus(status))
            .and(ContractSpecification.startDateFrom(startDateFrom))
            .and(ContractSpecification.startDateTo(startDateTo))
            .and(ContractSpecification.endDateFrom(endDateFrom))
            .and(ContractSpecification.endDateTo(endDateTo));

        return contractRepository.findAll(spec).stream()
            .map(contractMapper::toContractResponseDTO)
            .collect(Collectors.toList());
    }

    public boolean isContractExpired(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " not found."));
        return contract.getEndDate().isBefore(LocalDate.now());
    }

    public int getDaysUntilExpiration(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " not found."));
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), contract.getEndDate());
    }
}
