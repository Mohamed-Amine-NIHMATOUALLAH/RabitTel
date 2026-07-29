package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.PlanResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PlanService {

    PlanResponseDTO createPlan(PlanCreateRequestDTO dto);

    PlanResponseDTO updatePlan(UUID id, PlanUpdateRequestDTO dto);

    void deletePlan(UUID id);

    PlanResponseDTO getPlanById(UUID id);

    List<PlanResponseDTO> getAllPlans();

    List<PlanResponseDTO> searchPlans(
            Boolean active,
            String name,
            BigDecimal priceFrom,
            BigDecimal priceTo
    );
}