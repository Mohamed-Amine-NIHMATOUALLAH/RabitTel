package com.rabittel.lignesservice.repositories;

import com.rabittel.lignesservice.entities.Agency;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, UUID> , JpaSpecificationExecutor<Agency> {
    boolean existsByName(@NotBlank String name);

    boolean existsByDirectorateCode(@NotBlank String directorateCode);

    List<Agency> findByActive(Boolean active);
    List<Agency> findByDirectorateCode(String code);
    List<Agency> findByRegion(String region);
    List<Agency> findByNameContainingIgnoreCase(String name);}
