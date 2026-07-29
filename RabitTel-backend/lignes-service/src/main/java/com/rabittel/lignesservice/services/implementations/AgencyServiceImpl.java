package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.AgencyRequestDTO.AgencyUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.AgencyResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.AgencyMapper;
import com.rabittel.lignesservice.repositories.AgencyRepository;
import com.rabittel.lignesservice.services.interfaces.AgencyService;
import com.rabittel.lignesservice.specifications.AgencySpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class
AgencyServiceImpl implements AgencyService {
    private final AgencyRepository agencyRepository;
    private final AgencyMapper agencyMapper;

    @Transactional
    public AgencyResponseDTO createAgency(AgencyCreateRequestDTO dto) {

        if (agencyRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Agency already exists with name: " + dto.getName());
        }

        if (agencyRepository.existsByDirectorateCode(dto.getDirectorateCode())) {
            throw new ResourceAlreadyExistsException(
                    "Agency already exists with directorate code: " + dto.getDirectorateCode());
        }

        Agency agency = agencyMapper.toEntity(dto);

        Agency savedAgency = agencyRepository.save(agency);

        return agencyMapper.toAgencyResponseDTO(savedAgency);
    }

    @Transactional
    public AgencyResponseDTO updateAgency(UUID id, AgencyUpdateRequestDTO dto) {

        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agency not found with id: " + id));

        if (!Objects.equals(agency.getName(), dto.getName())
                && agencyRepository.existsByName(dto.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Agency already exists with name: " + dto.getName());
        }

        if (!Objects.equals(
                agency.getDirectorateCode(),
                dto.getDirectorateCode())
                && agencyRepository.existsByDirectorateCode(dto.getDirectorateCode())) {

            throw new ResourceAlreadyExistsException(
                    "Agency already exists with directorate code: "
                            + dto.getDirectorateCode());
        }

        agencyMapper.updateEntityFromDto(dto, agency);

        Agency updatedAgency = agencyRepository.save(agency);

        return agencyMapper.toAgencyResponseDTO(updatedAgency);
    }

    @Transactional
    public void softDeleteAgency(UUID id) {

        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agency not found with id: " + id));

        if (!agency.getLines().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot deactivate agency because it still has assigned lines.");
        }

        if (Boolean.TRUE.equals(agency.getActive())) {
            agency.setActive(false);
            agencyRepository.save(agency);
        }
    }

    //Admin
    @Transactional
    public void deleteAgency(UUID id) {

        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agency not found with id: " + id));

        if (!agency.getLines().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete agency because it still has assigned lines.");
        }

        if (Boolean.TRUE.equals(agency.getActive())) {
            throw new IllegalStateException(
                    "Cannot delete an active agency. Deactivate it first.");
        }

        agencyRepository.delete(agency);
    }

    @Transactional
    public List<AgencyResponseDTO> getAllAgencies() {
        return agencyRepository.findAll().stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AgencyResponseDTO getAgencyById(UUID id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + id + " not found."));
        return agencyMapper.toAgencyResponseDTO(agency);
    }

    @Transactional
    public List<AgencyResponseDTO> getAgenciesByActiveStatus(Boolean active) {
        return agencyRepository.findByActive(active).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AgencyResponseDTO> getAgenciesByDirectorateCode(String directorateCode) {
        return agencyRepository.findByDirectorateCode(directorateCode).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AgencyResponseDTO> getAgenciesByName(String name) {
        return agencyRepository.findByNameContainingIgnoreCase(name).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AgencyResponseDTO> getAgenciesByRegion(String region) {
        return agencyRepository.findByRegion(region).stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
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