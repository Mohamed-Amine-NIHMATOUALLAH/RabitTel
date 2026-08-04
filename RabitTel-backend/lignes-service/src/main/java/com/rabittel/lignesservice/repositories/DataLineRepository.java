package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.DataLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataLineRepository extends JpaRepository<DataLine, UUID>, JpaSpecificationExecutor<DataLine> {
    boolean existsByLineNumber(String lineNumber);
    boolean existsByIpAddress(String ipAddress);
    Optional<DataLine> findByLineNumber(String lineNumber);

    List<DataLine> findByLineStatus(LineStatus lineStatus);
}
