package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.RTCLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.services.implementations.RTCLineServiceImpl;
import com.rabittel.lignesservice.services.interfaces.RTCLineService;
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
@RequestMapping("/api/lines/rtc")
@AllArgsConstructor
@Tag(name = "RTC Lines", description = "Gestion des lignes fixes RTC")
public class RTCLineController {

    private final RTCLineService rtcLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne RTC")
    public RTCLineResponseDTO create(@Valid @RequestBody RTCLineCreateRequestDTO dto) {
        return rtcLineService.createRTCLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne RTC existante")
    public RTCLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody RTCLineUpdateRequestDTO dto) {
        return rtcLineService.updateRTCLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne RTC (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        rtcLineService.terminatedRTCLine(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne RTC (si non active)")
    public void delete(@PathVariable UUID id) {
        rtcLineService.deleteRTCLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes RTC avec filtres combinables")
    public List<RTCLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus
    ) {
        if (lineNumber == null && lineStatus == null) {
            return rtcLineService.getAllRTCLines();
        }
        return rtcLineService.searchRTCLines(lineNumber, lineStatus);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne RTC par son id")
    public RTCLineResponseDTO findById(@PathVariable UUID id) {
        return rtcLineService.getRTCLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne RTC par son numéro")
    public RTCLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return rtcLineService.getRTCLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes RTC facturables (Actif + Suspendu)")
    public List<RTCLineResponseDTO> findBillable() {
        return rtcLineService.getAllBillableRTCLines();
    }
}