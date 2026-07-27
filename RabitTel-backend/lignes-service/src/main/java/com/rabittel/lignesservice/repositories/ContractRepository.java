package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.Contract;
import com.rabittel.lignesservice.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID>, JpaSpecificationExecutor<Contract> {
    List<Contract> findByStatus(ContractStatus status);

    @Query("SELECT c FROM Contract c WHERE c.endDate < :thresholdDate AND c.status = 'ACTIVE'")
    List<Contract> findContractsExpiringBefore(@Param("thresholdDate") LocalDate thresholdDate);

    @Query("SELECT c FROM Contract c WHERE c.startDate >= :startDate AND c.endDate <= :endDate")
    List<Contract> findContractsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
