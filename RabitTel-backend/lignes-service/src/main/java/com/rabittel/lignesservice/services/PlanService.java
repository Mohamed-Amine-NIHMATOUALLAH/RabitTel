package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.response.PlanResponseDTO;
import com.rabittel.lignesservice.entities.Plan;
import com.rabittel.lignesservice.mappers.PlanMapper;
import com.rabittel.lignesservice.repositories.PlanRepository;
import com.rabittel.lignesservice.specifications.PlanSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    public List<PlanResponseDTO> searchPlans(Boolean active, String name, BigDecimal priceFrom, BigDecimal priceTo) {
        Specification<Plan> spec = Specification
                .<Plan>where(PlanSpecification.hasActive(active))
                .and(PlanSpecification.nameContains(name))
                .and(PlanSpecification.priceFrom(priceFrom))
                .and(PlanSpecification.priceTo(priceTo));

        return planRepository.findAll(spec).stream()
                .map(planMapper::toPlanResponseDTO)
                .collect(Collectors.toList());
    }
}
