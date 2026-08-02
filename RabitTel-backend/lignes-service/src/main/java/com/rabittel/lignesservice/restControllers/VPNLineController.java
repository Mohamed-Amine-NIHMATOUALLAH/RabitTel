package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.VPNLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.VPNLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.VPNLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.services.interfaces.VPNLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lines/vpn-adsl")
@AllArgsConstructor
@Tag(name = "VPN ADSL Lines", description = "Gestion des lignes VPN ADSL")
public class VPNLineController {

    private final VPNLineService vpnLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne VPN ADSL")
    public VPNLineResponseDTO create(@Valid @RequestBody VPNLineCreateRequestDTO dto) {
        return vpnLineService.createVPNLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne VPN ADSL existante")
    public VPNLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody VPNLineUpdateRequestDTO dto) {
        return vpnLineService.updateVPNLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne VPN ADSL (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        vpnLineService.terminatedVPNLine(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne VPN ADSL (si non active)")
    public void delete(@PathVariable UUID id) {
        vpnLineService.deleteVPNLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes VPN ADSL avec filtres combinables")
    public List<VPNLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) Long bandwidth,
            @RequestParam(required = false) String ipAddress
    ) {
        if (lineNumber == null && lineStatus == null && bandwidth == null && ipAddress == null) {
            return vpnLineService.getAllVPNLines();
        }
        return vpnLineService.searchVPNLines(lineNumber, lineStatus, bandwidth, ipAddress);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne VPN ADSL par son id")
    public VPNLineResponseDTO findById(@PathVariable UUID id) {
        return vpnLineService.getVPNLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne VPN ADSL par son numéro")
    public VPNLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return vpnLineService.getVPNLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes VPN ADSL facturables (Actif + Suspendu)")
    public List<VPNLineResponseDTO> findBillable() {
        return vpnLineService.getAllBillableVPNLines();
    }
}