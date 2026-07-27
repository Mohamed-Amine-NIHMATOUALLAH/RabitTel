package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.AgencyResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.AgencyMapper;
import com.rabittel.lignesservice.repositories.AgencyRepository;
import com.rabittel.lignesservice.specifications.AgencySpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AgencyService {
    private final AgencyRepository agencyRepository;
    private final AgencyMapper agencyMapper;

    public AgencyResponseDTO createAgency(AgencyCreateRequestDTO agencyCreateRequestDTO) {
        Agency agency = agencyMapper.toEntity(agencyCreateRequestDTO);

        if (agencyRepository.existsByName(agency.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Agency with name " + agency.getName() + " already exists.");
        }

        if (agencyRepository.existsByDirectorateCode(agency.getDirectorateCode())) {
            throw new ResourceAlreadyExistsException(
                    "Agency with directorate code " + agency.getDirectorateCode() + " already exists.");
        }

        Agency savedAgency = agencyRepository.save(agency);
        return agencyMapper.toAgencyResponseDTO(savedAgency);
    }

    public AgencyResponseDTO updateAgency(UUID id, AgencyUpdateRequestDTO agencyUpdateRequestDTO) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + id + " not found."));

        if (!agency.getName().equals(agencyUpdateRequestDTO.getName())
                && agencyRepository.existsByName(agencyUpdateRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Agency with name " + agencyUpdateRequestDTO.getName() + " already exists.");
        }

        if (!agency.getDirectorateCode().equals(agencyUpdateRequestDTO.getDirectorateCode())
                && agencyRepository.existsByDirectorateCode(agencyUpdateRequestDTO.getDirectorateCode())) {
            throw new ResourceAlreadyExistsException(
                    "Agency with directorate code " + agencyUpdateRequestDTO.getDirectorateCode() + " already exists.");
        }

        agencyMapper.updateEntityFromDto(agencyUpdateRequestDTO, agency);
        Agency updatedAgency = agencyRepository.save(agency);
        return agencyMapper.toAgencyResponseDTO(updatedAgency);
    }

    public void softDeleteAgency(UUID id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + id + " not found."));

        if (agency.getLines() != null && !agency.getLines().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete agency with active lines - transfer them first");
        }
        if (agency.getActive()) {
            agency.setActive(false);
            agencyRepository.save(agency);
        }
    }
//Admin
    public void deleteAgency(UUID id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + id + " not found."));

        if (agency.getLines() != null && !agency.getLines().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete agency with active lines - transfer them first");
        }

        if (agency.getActive()) {
            throw new IllegalStateException(
                    "Cannot delete an active agency - deactivate it first");
        }

        agencyRepository.delete(agency);
    }

    public List<AgencyResponseDTO> getAllAgencies() {
        return agencyRepository.findAll().stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    public AgencyResponseDTO getAgencyById(UUID id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + id + " not found."));
        return agencyMapper.toAgencyResponseDTO(agency);
    }

    public List<AgencyResponseDTO> getAgenciesByActiveStatus(Boolean active) {
        return agencyRepository.findByActive(active).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AgencyResponseDTO> getAgenciesByDirectorateCode(String directorateCode) {
        return agencyRepository.findByDirectorateCode(directorateCode).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AgencyResponseDTO> getAgenciesByName(String name) {
        return agencyRepository.findByNameContainingIgnoreCase(name).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AgencyResponseDTO> getAgenciesByRegion(String region) {
        return agencyRepository.findByRegion(region).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AgencyResponseDTO> searchAgencies(Boolean active, String region, String directorateCode, String name) {
        Specification<Agency> spec = Specification
                .<Agency>where(AgencySpecification.hasActive(active))
                .and(AgencySpecification.hasRegion(region))
                .and(AgencySpecification.hasDirectorateCode(directorateCode))
                .and(AgencySpecification.nameContains(name));

        return agencyRepository.findAll(spec).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }
}