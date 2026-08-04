package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractRenewalRequestDTO;
import com.rabittel.lignesservice.dtos.response.ContractResponseDTO;
import com.rabittel.lignesservice.entities.Contract;
import com.rabittel.lignesservice.enums.ContractStatus;
import com.rabittel.lignesservice.exceptions.BusinessRuleException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.ContractMapper;
import com.rabittel.lignesservice.repositories.ContractRepository;
import com.rabittel.lignesservice.repositories.LineRepository;
import com.rabittel.lignesservice.services.interfaces.ContractService;
import com.rabittel.lignesservice.specifications.ContractSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final LineRepository lineRepository;
    private static final int MAX_CONTRACT_DURATION_MONTHS = 120;

    private ContractResponseDTO mapToResponse(Contract contract) {
        ContractResponseDTO dto = contractMapper.toContractResponseDTO(contract);
        dto.setLinesCount(lineRepository.countByContractId(contract.getId()));
        return dto;
    }

    @Transactional
    public ContractResponseDTO createContract(ContractCreateRequestDTO contractCreateRequestDTO) {
        if (contractCreateRequestDTO.getStartDate() == null) {
            throw new BusinessRuleException("Start date cannot be null");
        }

        if (contractCreateRequestDTO.getDurationMonths() == null || contractCreateRequestDTO.getDurationMonths() < 1) {
            throw new BusinessRuleException("Duration must be at least 1 month");
        }

        if (contractCreateRequestDTO.getDurationMonths() > MAX_CONTRACT_DURATION_MONTHS) {
            throw new BusinessRuleException(
                    "Contract duration cannot exceed " + MAX_CONTRACT_DURATION_MONTHS + " months.");
        }

        if(contractCreateRequestDTO.getStartDate().isBefore(LocalDate.now())){
            throw new BusinessRuleException(
                    "Contract start date cannot be in the past.");
        }

        Contract contract = contractMapper.toEntity(contractCreateRequestDTO);
        LocalDate endDate = contract.getStartDate().plusMonths(contract.getDurationMonths());
        contract.setEndDate(endDate);
        contract.setStatus(ContractStatus.IN_PROGRESS);

        Contract savedContract = contractRepository.save(contract);
        return mapToResponse(savedContract);
    }

    @Transactional
    public ContractResponseDTO renewContract(UUID id, ContractRenewalRequestDTO renewalRequestDTO) {
        Contract contract = findContractById(id);

        if (renewalRequestDTO.getNewDurationMonths() == null || renewalRequestDTO.getNewDurationMonths() < 1) {
            throw new BusinessRuleException("Duration must be at least 1 month");
        }
        if (renewalRequestDTO.getNewDurationMonths() > MAX_CONTRACT_DURATION_MONTHS) {
            throw new BusinessRuleException(
                    "Contract duration cannot exceed " + MAX_CONTRACT_DURATION_MONTHS + " months.");
        }

        LocalDate newStartDate;
        if(contract.getEndDate().isBefore(LocalDate.now())){
             newStartDate = contract.getEndDate();
        } else {
             newStartDate = LocalDate.now();
        }

        contract.setStartDate(newStartDate);


        contract.setDurationMonths(contract.getDurationMonths() + renewalRequestDTO.getNewDurationMonths());

        LocalDate newEndDate =
                newStartDate.plusMonths(
                        renewalRequestDTO.getNewDurationMonths()
                );

        contract.setEndDate(newEndDate);

        if (contract.getEndDate().isBefore(LocalDate.now())) {
            contract.setStatus(ContractStatus.EXPIRED);
        } else {
            contract.setStatus(ContractStatus.RENEWED);
        }

        Contract updatedContract = contractRepository.save(contract);
        return mapToResponse(updatedContract);
    }

    @Transactional
    public void deleteContract(UUID id) {
        Contract contract = findContractById(id);

        if (lineRepository.countByContractId(id) > 0) {
            throw new BusinessRuleException(
                "Cannot delete contract with associated lines - delete or transfer lines first");
        }

        contractRepository.delete(contract);
    }

    @Transactional
    public List<ContractResponseDTO> getAllContracts() {
        return contractRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ContractResponseDTO getContractById(UUID id) {
        Contract contract = findContractById(id);
        return mapToResponse(contract);
    }

    @Transactional
    public List<ContractResponseDTO> getContractsByStatus(ContractStatus status) {
        return contractRepository.findByStatus(status).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<ContractResponseDTO> getActiveContracts() {
        List<ContractResponseDTO> activeContracts = getContractsByStatus(ContractStatus.IN_PROGRESS);
        activeContracts.addAll(getContractsByStatus(ContractStatus.RENEWED));
        return activeContracts;
    }

    @Transactional
    public List<ContractResponseDTO> getExpiredContracts() {

        return getContractsByStatus(ContractStatus.EXPIRED);
    }

    @Transactional
    public List<ContractResponseDTO> getExpiringContracts(int daysThreshold) {
        if(daysThreshold < 0){
            throw new BusinessRuleException(
                    "Days threshold cannot be negative.");
        }
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return contractRepository.findContractsExpiringBefore(thresholdDate).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<ContractResponseDTO> getContractsByDateRange(LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate)){
            throw new BusinessRuleException(
                    "Start date cannot be after end date.");
        }
        return contractRepository.findContractsByDateRange(startDate, endDate).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<ContractResponseDTO> searchContracts(ContractStatus status, LocalDate startDateFrom, LocalDate startDateTo,
                                                     LocalDate endDateFrom, LocalDate endDateTo) {
        Specification<Contract> spec = ContractSpecification.hasStatus(status)
            .and(ContractSpecification.startDateFrom(startDateFrom))
            .and(ContractSpecification.startDateTo(startDateTo))
            .and(ContractSpecification.endDateFrom(endDateFrom))
            .and(ContractSpecification.endDateTo(endDateTo));

        return contractRepository.findAll(spec).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public boolean isContractExpired(UUID contractId) {
        Contract contract = findContractById(contractId);
        return !contract.getEndDate().isAfter(LocalDate.now());
    }

    public Long getDaysUntilExpiration(UUID contractId) {
        Contract contract = findContractById(contractId);
        return ChronoUnit.DAYS.between(
                LocalDate.now(),
                contract.getEndDate());
    }

    private Contract findContractById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract with id " + id + " not found."));
    }

    @Transactional
    public void updateExpiredContracts(){

        int updated =
                contractRepository.updateExpiredContracts();

        System.out.println(
                updated + " contracts expired"
        );
    }
}
