package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.AgencyResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AgencyService {

    AgencyResponseDTO createAgency(AgencyCreateRequestDTO dto);

    AgencyResponseDTO updateAgency(UUID id, AgencyUpdateRequestDTO dto);

    void softDeleteAgency(UUID id);

    List<AgencyResponseDTO> getAgenciesByActiveStatus(Boolean active);

    List<AgencyResponseDTO> getAgenciesByDirectorateCode(String directorateCode);

    List<AgencyResponseDTO> getAgenciesByName(String name);

    List<AgencyResponseDTO> getAgenciesByRegion(String region);

    void deleteAgency(UUID id);

    AgencyResponseDTO getAgencyById(UUID id);

    List<AgencyResponseDTO> getAllAgencies();

    List<AgencyResponseDTO> searchAgencies(Boolean active, String region, String directorateCode, String name);
}