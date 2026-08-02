package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.FTTHLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.services.interfaces.FTTHLineService;
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
@RequestMapping("/api/lines/ftth")
@AllArgsConstructor
@Tag(name = "FTTH Lines", description = "Gestion des lignes fibre optique FTTH")
public class FTTHLineController {

    private final FTTHLineService ftthLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne FTTH")
    public FTTHLineResponseDTO create(@Valid @RequestBody FTTHLineCreateRequestDTO dto) {
        return ftthLineService.createFTTHLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne FTTH existante")
    public FTTHLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody FTTHLineUpdateRequestDTO dto) {
        return ftthLineService.updateFTTHLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne FTTH (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        ftthLineService.terminatedFTTHLine(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne FTTH (si non active)")
    public void delete(@PathVariable UUID id) {
        ftthLineService.deleteFTTHLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes FTTH avec filtres combinables")
    public List<FTTHLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) String fixedLineNumber,
            @RequestParam(required = false) String routerBrand,
            @RequestParam(required = false) Long bandwidth
    ) {
        if (lineNumber == null && lineStatus == null && fixedLineNumber == null
                && routerBrand == null && bandwidth == null) {
            return ftthLineService.getAllFTTHLines();
        }
        return ftthLineService.searchFTTHLines(lineNumber, lineStatus, fixedLineNumber, routerBrand, bandwidth);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne FTTH par son id")
    public FTTHLineResponseDTO findById(@PathVariable UUID id) {
        return ftthLineService.getFTTHLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne FTTH par son numéro")
    public FTTHLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return ftthLineService.getFTTHLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes FTTH facturables (Actif + Suspendu)")
    public List<FTTHLineResponseDTO> findBillable() {
        return ftthLineService.getAllBillableFTTHLines();
    }
}