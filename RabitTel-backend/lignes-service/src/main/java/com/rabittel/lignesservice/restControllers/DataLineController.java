package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.DataLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.DataLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.DataLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.services.interfaces.DataLineService;
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
@RequestMapping("/api/lines/data")
@AllArgsConstructor
@Tag(name = "Data Lines", description = "Gestion des lignes Data")
public class DataLineController {

    private final DataLineService dataLineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle ligne Data")
    public DataLineResponseDTO create(@Valid @RequestBody DataLineCreateRequestDTO dto) {
        return dataLineService.createDataLine(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ligne Data existante")
    public DataLineResponseDTO update(@PathVariable UUID id, @Valid @RequestBody DataLineUpdateRequestDTO dto) {
        return dataLineService.updateDataLine(id, dto);
    }

    @PatchMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Résilier une ligne Data (passage au statut TERMINATED)")
    public void terminate(@PathVariable UUID id) {
        dataLineService.terminateDataLine(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une ligne Data (si non active)")
    public void delete(@PathVariable UUID id) {
        dataLineService.deleteDataLine(id);
    }

    @GetMapping
    @Operation(summary = "Lister les lignes Data avec filtres combinables")
    public List<DataLineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) LineType lineType,
            @RequestParam(required = false) String bandwidth,
            @RequestParam(required = false) String ipAddress
    ) {
        if (lineNumber == null && lineStatus == null && bandwidth == null && ipAddress == null) {
            return dataLineService.getAllDataLines(lineType);
        }
        return dataLineService.searchDataLines(lineNumber, lineStatus, lineType, bandwidth, ipAddress);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne Data par son id")
    public DataLineResponseDTO findById(@PathVariable UUID id) {
        return dataLineService.getDataLineById(id);
    }

    @GetMapping("/number/{lineNumber}")
    @Operation(summary = "Récupérer une ligne Data par son numéro")
    public DataLineResponseDTO findByLineNumber(@PathVariable String lineNumber) {
        return dataLineService.getDataLineByLineNumber(lineNumber);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister les lignes Data facturables (Actif + Suspendu)")
    public List<DataLineResponseDTO> findBillable(
            @RequestParam(required = false) LineType lineType
    ) {
        return dataLineService.getAllBillableDataLines(lineType);
    }
}