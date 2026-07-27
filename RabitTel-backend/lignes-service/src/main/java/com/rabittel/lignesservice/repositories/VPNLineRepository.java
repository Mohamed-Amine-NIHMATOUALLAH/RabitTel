package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.VPNLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VPNLineRepository extends JpaRepository<VPNLine, UUID>, JpaSpecificationExecutor<VPNLine> {
    boolean existsByLineNumber(String lineNumber);
    boolean existsByIpAddress(String ipAddress);
    Optional<VPNLine> findByLineNumber(String lineNumber);

    List<VPNLine> findByLineStatus(LineStatus lineStatus);
}
