package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.ContractRequestDTO.ContractRenewalRequestDTO;
import com.rabittel.lignesservice.dtos.response.ContractResponseDTO;
import com.rabittel.lignesservice.enums.ContractStatus;
import com.rabittel.lignesservice.services.implementations.ContractServiceImpl;
import com.rabittel.lignesservice.services.interfaces.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
@AllArgsConstructor
@Tag(name = "Contracts", description = "Gestion des contrats")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un nouveau contrat")
    public ContractResponseDTO create(@Valid @RequestBody ContractCreateRequestDTO dto) {
        return contractService.createContract(dto);
    }

    @PatchMapping("/{id}/renew")
    @Operation(summary = "Renouveler un contrat existant")
    public ContractResponseDTO renew(@PathVariable UUID id, @Valid @RequestBody ContractRenewalRequestDTO dto) {
        return contractService.renewContract(id, dto);
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un contrat (si aucune ligne associée)")
    public void delete(@PathVariable UUID id) {
        contractService.deleteContract(id);
    }

    @GetMapping
    @Operation(summary = "Lister les contrats avec filtres combinables")
    public List<ContractResponseDTO> findAll(
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) LocalDate startDateFrom,
            @RequestParam(required = false) LocalDate startDateTo,
            @RequestParam(required = false) LocalDate endDateFrom,
            @RequestParam(required = false) LocalDate endDateTo
    ) {
        if (status == null && startDateFrom == null && startDateTo == null
                && endDateFrom == null && endDateTo == null) {
            return contractService.getAllContracts();
        }
        return contractService.searchContracts(status, startDateFrom, startDateTo, endDateFrom, endDateTo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un contrat par son id")
    public ContractResponseDTO findById(@PathVariable UUID id) {
        return contractService.getContractById(id);
    }

    @GetMapping("/active")
    @Operation(summary = "Lister les contrats en cours ou renouvelés")
    public List<ContractResponseDTO> findActive() {
        return contractService.getActiveContracts();
    }

    @GetMapping("/expired")
    @Operation(summary = "Lister les contrats expirés")
    public List<ContractResponseDTO> findExpired() {
        return contractService.getExpiredContracts();
    }

    @GetMapping("/expiring")
    @Operation(summary = "Lister les contrats arrivant à expiration avant N jours")
    public List<ContractResponseDTO> findExpiring(@RequestParam(defaultValue = "30") int daysThreshold) {
        return contractService.getExpiringContracts(daysThreshold);
    }

    @GetMapping("/{id}/days-until-expiration")
    @Operation(summary = "Nombre de jours restants avant l'expiration d'un contrat")
    public Long daysUntilExpiration(@PathVariable UUID id) {
        return contractService.getDaysUntilExpiration(id);
    }
}