package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.VPN4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.VPN4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.VPN4GLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.services.implementations.VPN4GLineServiceImpl;
import com.rabittel.lignesservice.services.interfaces.VPN4GLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lines/4g-vpn")
@AllArgsConstructor
@Tag(name = "4G VPN Lines", description = "Gestion des lignes VPN 4G")
public class VPN4GLineController {

    private final VPN4GLineService vpn4GLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne VPN 4G")
    public VPN4GLineResponseDTO create(@Valid @RequestBody VPN4GLineCreateRequestDTO dto) {
        return vpn4GLineService.createVPN4GLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne VPN 4G existante")
    public VPN4GLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody VPN4GLineUpdateRequestDTO dto) {
        return vpn4GLineService.updateVPN4GLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne VPN 4G (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        vpn4GLineService.terminatedVPN4GLine(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne VPN 4G (si non active)")
    public void delete(@PathVariable UUID id) {
        vpn4GLineService.deleteVPN4GLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes VPN 4G avec filtres combinables")
    public List<VPN4GLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) String equipment,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) LocalDate deliveryDateFrom,
            @RequestParam(required = false) LocalDate deliveryDateTo
    ) {
        if (lineNumber == null && lineStatus == null && equipment == null && ipAddress == null
                && serialNumber == null && deliveryDateFrom == null && deliveryDateTo == null) {
            return vpn4GLineService.getAllVPN4GLines();
        }
        return vpn4GLineService.searchVPN4GLines(lineNumber, lineStatus, equipment, ipAddress, serialNumber,
                deliveryDateFrom, deliveryDateTo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne VPN 4G par son id")
    public VPN4GLineResponseDTO findById(@PathVariable UUID id) {
        return vpn4GLineService.getVPN4GLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne VPN 4G par son numéro")
    public VPN4GLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return vpn4GLineService.getVPN4GLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes VPN 4G facturables (Actif + Suspendu)")
    public List<VPN4GLineResponseDTO> findBillable() {
        return vpn4GLineService.getAllBillableVPN4GLines();
    }
}