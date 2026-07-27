package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.RTCLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RTCLineRepository extends JpaRepository<RTCLine, UUID>, JpaSpecificationExecutor<RTCLine> {
    boolean existsByLineNumber(String lineNumber);
    Optional<RTCLine> findByLineNumber(String lineNumber);

    List<RTCLine> findByLineStatus(LineStatus lineStatus);
}
