package com.rabittel.lignesservice.restControllers;

import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.PlanResponseDTO;
import com.rabittel.lignesservice.services.implementations.PlanServiceImpl;
import com.rabittel.lignesservice.services.interfaces.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
@AllArgsConstructor
@Tag(name = "Plans", description = "Gestion des forfaits")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un nouveau forfait")
    public PlanResponseDTO create(@Valid @RequestBody PlanCreateRequestDTO dto) {
        return planService.createPlan(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un forfait existant")
    public PlanResponseDTO update(@PathVariable UUID id, @Valid @RequestBody PlanUpdateRequestDTO dto) {
        return planService.updatePlan(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un forfait")
    public void delete(@PathVariable UUID id) {
        planService.deletePlan(id);
    }

    @GetMapping
    @Operation(summary = "Lister les forfaits avec filtres combinables")
    public List<PlanResponseDTO> findAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal priceFrom,
            @RequestParam(required = false) BigDecimal priceTo
    ) {
        if (active == null && name == null && priceFrom == null && priceTo == null) {
            return planService.getAllPlans();
        }
        return planService.searchPlans(active, name, priceFrom, priceTo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un forfait par son id")
    public PlanResponseDTO findById(@PathVariable UUID id) {
        return planService.getPlanById(id);
    }
}