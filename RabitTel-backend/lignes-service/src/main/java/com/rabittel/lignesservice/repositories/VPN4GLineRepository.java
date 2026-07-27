package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.VPN4GLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VPN4GLineRepository extends JpaRepository<VPN4GLine, UUID>, JpaSpecificationExecutor<VPN4GLine> {
    boolean existsByLineNumber(String lineNumber);
    boolean existsByIpAddress(String ipAddress);
    boolean existsBySerialNumber(String serialNumber);
    Optional<VPN4GLine> findByLineNumber(String lineNumber);

    List<VPN4GLine> findByLineStatus(LineStatus lineStatus);
}
