package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.VPNLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.VPNLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.VPNLineResponseDTO;
import com.rabittel.lignesservice.entities.VPNLine;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.VPNLineMapper;
import com.rabittel.lignesservice.repositories.VPNLineRepository;
import com.rabittel.lignesservice.specifications.LineSpecification;
import com.rabittel.lignesservice.specifications.VPNLineSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VPNLineService {
    private final VPNLineRepository vpnLineRepository;
    private final VPNLineMapper vpnLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.PlanRepository planRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public VPNLineResponseDTO createVPNLine(VPNLineCreateRequestDTO createRequestDTO) {
        if (vpnLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("VPN Line with number " + createRequestDTO.getLineNumber() + " already exists.");
        }

        if (vpnLineRepository.existsByIpAddress(createRequestDTO.getIpAddress())) {
            throw new ResourceAlreadyExistsException("IP address " + createRequestDTO.getIpAddress() + " already exists.");
        }

        VPNLine vpnLine = vpnLineMapper.toEntity(createRequestDTO);
        VPNLine savedLine = vpnLineRepository.save(vpnLine);
        return vpnLineMapper.toVPNLineResponseDTO(savedLine);
    }

    public VPNLineResponseDTO updateVPNLine(UUID id, VPNLineUpdateRequestDTO updateRequestDTO) {

        VPNLine vpnLine = vpnLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VPN Line with id " + id + " not found."
                ));

        // Vérification de l'unicité du numéro de ligne
        if (updateRequestDTO.getLineNumber() != null
                && !updateRequestDTO.getLineNumber().equals(vpnLine.getLineNumber())
                && vpnLineRepository.existsByLineNumber(updateRequestDTO.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "VPN Line with number "
                            + updateRequestDTO.getLineNumber()
                            + " already exists."
            );
        }

        // Vérification de l'unicité de l'adresse IP
        if (updateRequestDTO.getIpAddress() != null
                && !updateRequestDTO.getIpAddress().equals(vpnLine.getIpAddress())
                && vpnLineRepository.existsByIpAddress(updateRequestDTO.getIpAddress())) {

            throw new ResourceAlreadyExistsException(
                    "IP address "
                            + updateRequestDTO.getIpAddress()
                            + " already exists."
            );
        }

        // Mise à jour des champs simples
        vpnLineMapper.updateEntityFromDto(updateRequestDTO, vpnLine);

        // Mise à jour des relations
        if (updateRequestDTO.getAgencyId() != null) {
            var agency = agencyRepository.findById(updateRequestDTO.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Agency with id " + updateRequestDTO.getAgencyId() + " not found."
                    ));
            vpnLine.setAgency(agency);
        }

        if (updateRequestDTO.getPlanId() != null) {
            var plan = planRepository.findById(updateRequestDTO.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Plan with id " + updateRequestDTO.getPlanId() + " not found."
                    ));
            vpnLine.setPlan(plan);
        }

        if (updateRequestDTO.getContractId() != null) {
            var contract = contractRepository.findById(updateRequestDTO.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract with id " + updateRequestDTO.getContractId() + " not found."
                    ));
            vpnLine.setContract(contract);
        }

        if (updateRequestDTO.getCreatedBy() != null) {
            vpnLine.setCreatedBy(updateRequestDTO.getCreatedBy());
        }

        VPNLine updatedLine = vpnLineRepository.save(vpnLine);

        return vpnLineMapper.toVPNLineResponseDTO(updatedLine);
    }


    public void terminatedVPNLine(UUID id) {

        VPNLine vpnLine = vpnLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VPN Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(vpnLine.getLineStatus())) {
            vpnLine.setLineStatus(LineStatus.TERMINATED);
            vpnLineRepository.save(vpnLine);
        }
    }


    public void deleteVPNLine(UUID id) {

        VPNLine vpnLine = vpnLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VPN Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(vpnLine.getLineStatus())) {
            throw new IllegalStateException(
                    "Cannot delete an active VPN Line."
            );
        }

        vpnLineRepository.delete(vpnLine);
    }

    public List<VPNLineResponseDTO> getAllVPNLines() {
        return vpnLineRepository.findAll().stream()
            .map(vpnLineMapper::toVPNLineResponseDTO)
            .collect(Collectors.toList());
    }

    public VPNLineResponseDTO getVPNLineById(UUID id) {
        VPNLine vpnLine = vpnLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("VPN Line with id " + id + " not found."));
        return vpnLineMapper.toVPNLineResponseDTO(vpnLine);
    }

    public VPNLineResponseDTO getVPNLineByLineNumber(String lineNumber) {
        VPNLine vpnLine = vpnLineRepository.findByLineNumber(lineNumber)
            .orElseThrow(() -> new ResourceNotFoundException("VPN Line with number " + lineNumber + " not found."));
        return vpnLineMapper.toVPNLineResponseDTO(vpnLine);
    }

    public List<VPNLineResponseDTO> getAllVPNLinesByStatus(LineStatus lineStatus) {
        return vpnLineRepository.findByLineStatus(lineStatus).stream()
                .map(vpnLineMapper::toVPNLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VPNLineResponseDTO> getAllBillableVPNLines(){
        List<VPNLineResponseDTO> vpnLines = getAllVPNLinesByStatus(LineStatus.ACTIVE);
        vpnLines.addAll(getAllVPNLinesByStatus(LineStatus.SUSPENDED));
        return vpnLines;
    }

    public List<VPNLineResponseDTO> searchVPNLines(
            String lineNumber,
            LineStatus lineStatus,
            String bandwidth,
            String ipAddress) {

        Specification<VPNLine> spec = Specification
                .<VPNLine>where(LineSpecification.hasLineNumber(lineNumber))
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(VPNLineSpecification.hasBandwidth(bandwidth))
                .and(VPNLineSpecification.hasIpAddress(ipAddress));

        return vpnLineRepository.findAll(spec).stream()
                .map(vpnLineMapper::toVPNLineResponseDTO)
                .collect(Collectors.toList());
    }

    private Specification<VPNLine> hasLineNumber(String lineNumber) {
        return (root, query, criteriaBuilder) -> {
            if (lineNumber == null || lineNumber.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("lineNumber"), lineNumber);
        };
    }

    private Specification<VPNLine> hasLineStatus(LineStatus lineStatus) {
        return (root, query, criteriaBuilder) -> {
            if (lineStatus == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("lineStatus"), lineStatus);
        };
    }

    private Specification<VPNLine> hasBandwidth(String bandwidth) {
        return (root, query, criteriaBuilder) -> {
            if (bandwidth == null || bandwidth.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("bandwidth"), bandwidth);
        };
    }

    private Specification<VPNLine> hasIpAddress(String ipAddress) {
        return (root, query, criteriaBuilder) -> {
            if (ipAddress == null || ipAddress.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("ipAddress"), ipAddress);
        };
    }
}
