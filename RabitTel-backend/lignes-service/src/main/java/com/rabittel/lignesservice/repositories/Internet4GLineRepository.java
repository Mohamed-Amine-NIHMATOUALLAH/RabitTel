package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.Internet4GLine;
import com.rabittel.lignesservice.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface Internet4GLineRepository extends JpaRepository<Internet4GLine, UUID>, JpaSpecificationExecutor<Internet4GLine> {
    boolean existsByLineNumber(String lineNumber);
    boolean existsBySimSerialNumber(String simSerialNumber);
    boolean existsByEquipmentSerialNumber(String equipmentSerialNumber);
    Optional<Internet4GLine> findByLineNumber(String lineNumber);

    List<Internet4GLine> findByLineStatus(LineStatus lineStatus);
}
