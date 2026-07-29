package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.response.LineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.services.LineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lines")
@AllArgsConstructor
@Tag(name = "Lines (vue globale)", description = "Consultation transverse de toutes les lignes, tous types confondus")
public class LineController {

    private final LineService lineService;

    @GetMapping
    @Operation(summary = "Lister toutes les lignes (tous types) avec filtres combinables")
    public List<LineResponseDTO> findAll(
            @RequestParam(required = false) String lineNumber,
            @RequestParam(required = false) LineStatus lineStatus,
            @RequestParam(required = false) LineType lineType,
            @RequestParam(required = false) UUID agencyId,
            @RequestParam(required = false) UUID planId
    ) {
        if (lineNumber == null && lineStatus == null && lineType == null && agencyId == null && planId == null) {
            return lineService.getAllLines();
        }
        return lineService.searchLines(lineNumber, lineStatus, lineType, agencyId, planId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne par son id, quel que soit son type")
    public LineResponseDTO findById(@PathVariable UUID id) {
        return lineService.getLineById(id);
    }

    @GetMapping("/billable")
    @Operation(summary = "Lister toutes les lignes facturables (Actif + Suspendu), tous types confondus")
    public List<LineResponseDTO> findBillable() {
        return lineService.getAllBillableLines();
    }
}