package com.rabittel.lignesservice.restControllers;


import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.AgencyResponseDTO;
import com.rabittel.lignesservice.services.AgencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agencies")
@AllArgsConstructor
@Tag(name = "Agencies", description = "Gestion des agences / directions régionales")
public class AgencyController {

    private final AgencyService agencyService;

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle agence")
    public AgencyResponseDTO create(@Valid @RequestBody AgencyCreateRequestDTO dto) {
        return agencyService.createAgency(dto);
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Modifier une agence existante")
    public AgencyResponseDTO update(@PathVariable UUID id, @Valid @RequestBody AgencyUpdateRequestDTO dto) {
        return agencyService.updateAgency(id, dto);
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désactiver une agence (soft delete)")
    public void deactivate(@PathVariable UUID id) {
        agencyService.softDeleteAgency(id);
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer définitivement une agence désactivée")
    public void delete(@PathVariable UUID id) {
        agencyService.deleteAgency(id);
    }


    @GetMapping
    @Operation(summary = "Lister les agences avec filtres combinables")
    public List<AgencyResponseDTO> findAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String directorateCode,
            @RequestParam(required = false) String name
    ) {
        if (active == null && region == null && directorateCode == null && name == null) {
            return agencyService.getAllAgencies();
        }
        return agencyService.searchAgencies(active, region, directorateCode, name);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une agence par son id")
    public AgencyResponseDTO findById(@PathVariable UUID id) {
        return agencyService.getAgencyById(id);
    }
}