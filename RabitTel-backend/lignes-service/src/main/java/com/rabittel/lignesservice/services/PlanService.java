package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanUpdateRequestDTO;
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

    public PlanResponseDTO createPlan(PlanCreateRequestDTO  planCreateRequestDTO) {
        if (planRepository.existsByName(planCreateRequestDTO.getName())) {
            throw new IllegalArgumentException("Plan with name " + planCreateRequestDTO.getName() + " already exists.");
        }
        Plan plan = planMapper.toEntity(planCreateRequestDTO);
        Plan savedPlan = planRepository.save(plan);
        return planMapper.toPlanResponseDTO(savedPlan);
    }

    public PlanResponseDTO updatePlan(Long id, PlanUpdateRequestDTO planUpdateRequestDTO) {

        Plan plan = null ;
        if (planRepository.findById(id)!= null) {
            plan = planRepository.findById(id);}

        if (plan == null) {
            throw new IllegalArgumentException("Plan with id " + id + " not found.");
        }

        if (!plan.getName().equals(planUpdateRequestDTO.getName())
                && planRepository.existsByName(planUpdateRequestDTO.getName())) {
            throw new IllegalArgumentException("Plan with name " + planUpdateRequestDTO.getName() + " already exists.");
        }

        planMapper.updatePlanFromRequestDTO(planUpdateRequestDTO, plan);
        Plan updatedPlan = planRepository.save(plan);

        return planMapper.toPlanResponseDTO(updatedPlan);
    }


    public void deletePlan (Long id) {
        Plan plan = planRepository.findById(id);
        if (plan == null) {
            throw new IllegalArgumentException("Plan with id " + id + " not found.");
        }
        if (plan.getLines().size() > 0) {
            throw new IllegalArgumentException("Cannot delete plan with id " + id + " because it is associated with existing lines.");
        }
        if (plan.getActive()) {
            throw new IllegalArgumentException("Cannot delete plan with id " + id + " because it is active.");
        }
        planRepository.delete(plan);
    }

    public PlanResponseDTO getPlanById(Long id) {
        Plan plan = planRepository.findById(id);
        if (plan == null) {
            throw new IllegalArgumentException("Plan with id " + id + " not found.");
        }
        return planMapper.toPlanResponseDTO(plan);
    }

    public List<PlanResponseDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(planMapper::toPlanResponseDTO)
                .collect(Collectors.toList());
    }

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
