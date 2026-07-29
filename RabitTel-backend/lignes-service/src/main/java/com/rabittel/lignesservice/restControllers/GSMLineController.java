package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.GSMLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.GSMLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.GSMLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.services.implementations.GSMLineServiceImpl;
import com.rabittel.lignesservice.services.interfaces.GSMLineService;
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
@RequestMapping("/api/lines/gsm")
@AllArgsConstructor
@Tag(name = "GSM Lines", description = "Gestion des lignes GSM Pro")
public class GSMLineController {

    private final GSMLineService gsmLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne GSM")
    public GSMLineResponseDTO create(@Valid @RequestBody GSMLineCreateRequestDTO dto) {
        return gsmLineService.createGSMLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne GSM existante")
    public GSMLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody GSMLineUpdateRequestDTO dto) {
        return gsmLineService.updateGSMLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne GSM (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        gsmLineService.terminatedGSMLine(id);
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne GSM (si non active)")
    public void delete(@PathVariable UUID id) {
        gsmLineService.deleteGSMLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes GSM avec filtres combinables")
    public List<GSMLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) String serviceFunction,
            @RequestParam(required = false) String chipSerialNumber,
            @RequestParam(required = false) LocalDate chipDeliveryDateFrom,
            @RequestParam(required = false) LocalDate chipDeliveryDateTo,
            @RequestParam(required = false) String pinCode,
            @RequestParam(required = false) String pukCode
    ) {
        if (lineNumber == null && lineStatus == null && serviceFunction == null && chipSerialNumber == null
                && chipDeliveryDateFrom == null && chipDeliveryDateTo == null && pinCode == null && pukCode == null) {
            return gsmLineService.getAllGSMLines();
        }
        return gsmLineService.searchGSMLines(lineNumber, lineStatus, serviceFunction, chipSerialNumber,
                chipDeliveryDateFrom, chipDeliveryDateTo, pinCode, pukCode);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne GSM par son id")
    public GSMLineResponseDTO findById(@PathVariable UUID id) {
        return gsmLineService.getGSMLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne GSM par son numéro")
    public GSMLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return gsmLineService.getGSMLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes GSM facturables (Actif + Suspendu)")
    public List<GSMLineResponseDTO> findBillable() {
        return gsmLineService.getAllBillableGSMLines();
    }
}