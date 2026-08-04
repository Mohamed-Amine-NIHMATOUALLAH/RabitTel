package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.GSMLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GSMLineRepository extends JpaRepository<GSMLine, UUID>, JpaSpecificationExecutor<GSMLine> {
    boolean existsByLineNumber(String lineNumber);
    boolean existsByChipSerialNumber(String chipSerialNumber);
    Optional<GSMLine> findByLineNumber(String lineNumber);

    List<GSMLine> findByLineStatus(LineStatus lineStatus);
    List<GSMLine> findByLineStatusIn(List<LineStatus> statuses);
    long countByPlanId(UUID planId);
}
