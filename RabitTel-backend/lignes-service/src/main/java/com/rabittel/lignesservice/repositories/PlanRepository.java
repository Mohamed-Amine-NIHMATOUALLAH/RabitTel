package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> , JpaSpecificationExecutor<Plan> {
}
