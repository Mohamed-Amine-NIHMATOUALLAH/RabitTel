package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.FTTHLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FTTHLineRepository extends JpaRepository<FTTHLine, UUID>, JpaSpecificationExecutor<FTTHLine> {
     List<FTTHLine> findByLineStatus(LineStatus lineStatus);

    boolean existsByLineNumber(String lineNumber);
    boolean existsByFixedLineNumber(String fixedLineNumber);
    Optional<FTTHLine> findByLineNumber(String lineNumber);
}
