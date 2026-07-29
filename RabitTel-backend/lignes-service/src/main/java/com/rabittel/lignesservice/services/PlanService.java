package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.PlanRequestDTO.PlanUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.PlanResponseDTO;
import com.rabittel.lignesservice.entities.Plan;
import com.rabittel.lignesservice.exceptions.BusinessRuleException;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.PlanMapper;
import com.rabittel.lignesservice.repositories.PlanRepository;
import com.rabittel.lignesservice.specifications.PlanSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    @Transactional
    public PlanResponseDTO createPlan(PlanCreateRequestDTO  planCreateRequestDTO) {
        if (planRepository.existsByName(planCreateRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Plan with name " + planCreateRequestDTO.getName() + " already exists.");
        }
        Plan plan = planMapper.toEntity(planCreateRequestDTO);
        Plan savedPlan = planRepository.save(plan);
        return planMapper.toPlanResponseDTO(savedPlan);
    }

    @Transactional
    public PlanResponseDTO updatePlan(UUID id, PlanUpdateRequestDTO planUpdateRequestDTO) {

        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan with id " + id + " not found."
                        ));

        if (planUpdateRequestDTO.getName() != null
                && !planUpdateRequestDTO.getName().equals(plan.getName())
                && planRepository.existsByName(planUpdateRequestDTO.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Plan with name "
                            + planUpdateRequestDTO.getName()
                            + " already exists."
            );
        }

        planMapper.updatePlanFromRequestDTO(planUpdateRequestDTO, plan);

        Plan updatedPlan = planRepository.save(plan);

        return planMapper.toPlanResponseDTO(updatedPlan);
    }

    @Transactional
    public void deletePlan (UUID id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Plan with id " + id + " not found."));
        if (!plan.getLines().isEmpty()) {
            throw new BusinessRuleException("Cannot delete plan with id " + id + " because it is associated with existing lines.");
        }
        if (plan.getActive()) {
            throw new BusinessRuleException("Cannot delete plan with id " + id + " because it is active.");
        }
        planRepository.delete(plan);
    }

    @Transactional
    public PlanResponseDTO getPlanById(UUID id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Plan with id " + id + " not found."));
        return planMapper.toPlanResponseDTO(plan);
    }

    @Transactional
    public List<PlanResponseDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(planMapper::toPlanResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
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
