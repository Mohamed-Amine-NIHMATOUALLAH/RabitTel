package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.Internet4GLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.services.interfaces.Internet4GLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lines/4g-internet")
@AllArgsConstructor
@Tag(name = "4G Internet Lines", description = "Gestion des lignes 4G Internet")
public class Internet4GLineController {

    private final Internet4GLineService internet4GLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne 4G Internet")
    public Internet4GLineResponseDTO create(@Valid @RequestBody Internet4GLineCreateRequestDTO dto) {
        return internet4GLineService.createInternet4GLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne 4G Internet existante")
    public Internet4GLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody Internet4GLineUpdateRequestDTO dto) {
        return internet4GLineService.updateInternet4GLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne 4G Internet (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        internet4GLineService.terminatedInternet4GLine(id);
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne 4G Internet (si non active)")
    public void delete(@PathVariable UUID id) {
        internet4GLineService.deleteInternet4GLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes 4G Internet avec filtres combinables")
    public List<Internet4GLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) String serviceFunction,
            @RequestParam(required = false) String simSerialNumber,
            @RequestParam(required = false) String pinCode,
            @RequestParam(required = false) String pukCode,
            @RequestParam(required = false) String equipment,
            @RequestParam(required = false) String equipmentSerialNumber,
            @RequestParam(required = false) Long bandwidth
    ) {
        if (lineNumber == null && lineStatus == null && serviceFunction == null && simSerialNumber == null
                && pinCode == null && pukCode == null && equipment == null
                && equipmentSerialNumber == null && bandwidth == null) {
            return internet4GLineService.getAllInternet4GLines();
        }
        return internet4GLineService.searchInternet4GLines(lineNumber, lineStatus, serviceFunction, simSerialNumber,
                pinCode, pukCode, equipment, equipmentSerialNumber, bandwidth);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne 4G Internet par son id")
    public Internet4GLineResponseDTO findById(@PathVariable UUID id) {
        return internet4GLineService.getInternet4GLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne 4G Internet par son numéro")
    public Internet4GLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return internet4GLineService.getInternet4GLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes 4G Internet facturables (Actif + Suspendu)")
    public List<Internet4GLineResponseDTO> findBillable() {
        return internet4GLineService.getAllBillableInternet4GLines();
    }
}