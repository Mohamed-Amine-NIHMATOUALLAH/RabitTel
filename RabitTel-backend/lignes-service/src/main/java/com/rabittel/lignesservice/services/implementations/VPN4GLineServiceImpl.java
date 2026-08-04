package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.VPN4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.VPN4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.VPN4GLineResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.entities.VPN4GLine;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.VPN4GLineMapper;
import com.rabittel.lignesservice.repositories.VPN4GLineRepository;
import com.rabittel.lignesservice.services.interfaces.VPN4GLineService;
import com.rabittel.lignesservice.specifications.LineSpecification;
import com.rabittel.lignesservice.specifications.VPN4GLineSpecification;
import com.rabittel.lignesservice.validation.LineValueUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VPN4GLineServiceImpl implements VPN4GLineService {
    private final VPN4GLineRepository vpn4GLineRepository;
    private final VPN4GLineMapper vpn4GLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public VPN4GLineResponseDTO createVPN4GLine(VPN4GLineCreateRequestDTO createRequestDTO) {
        createRequestDTO.setLineNumber(normalizeGsmLikeLineNumber(createRequestDTO.getLineNumber()));

        if (vpn4GLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("VPN 4G Line with number " + createRequestDTO.getLineNumber() + " already exists.");
        }

        if (vpn4GLineRepository.existsByIpAddress(createRequestDTO.getIpAddress())) {
            throw new ResourceAlreadyExistsException("IP address " + createRequestDTO.getIpAddress() + " already exists.");
        }

        if (vpn4GLineRepository.existsBySerialNumber(createRequestDTO.getSerialNumber())) {
            throw new ResourceAlreadyExistsException("Serial number " + createRequestDTO.getSerialNumber() + " already exists.");
        }

        VPN4GLine vpn4GLine = vpn4GLineMapper.toEntity(createRequestDTO);

        Agency agency = agencyRepository.findById(createRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        vpn4GLine.setAgency(agency);

        vpn4GLine.setLineType(LineType.G4_VPN);
        vpn4GLine.setLineStatus(LineStatus.ACTIVE);


        VPN4GLine savedLine = vpn4GLineRepository.save(vpn4GLine);

        return vpn4GLineMapper.toVPN4GLineResponseDTO(savedLine);
    }

    public VPN4GLineResponseDTO updateVPN4GLine(UUID id, VPN4GLineUpdateRequestDTO updateRequestDTO) {

        VPN4GLine vpn4GLine = vpn4GLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VPN 4G Line with id " + id + " not found."
                ));

        if (updateRequestDTO.getLineNumber() != null) {
            updateRequestDTO.setLineNumber(normalizeGsmLikeLineNumber(updateRequestDTO.getLineNumber()));
        }

        // Vérification de l'unicité du numéro de ligne
        if (updateRequestDTO.getLineNumber() != null
                && !updateRequestDTO.getLineNumber().equals(vpn4GLine.getLineNumber())
                && vpn4GLineRepository.existsByLineNumber(updateRequestDTO.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "VPN 4G Line with number "
                            + updateRequestDTO.getLineNumber()
                            + " already exists."
            );
        }

        // Vérification de l'unicité de l'adresse IP
        if (updateRequestDTO.getIpAddress() != null
                && !updateRequestDTO.getIpAddress().equals(vpn4GLine.getIpAddress())
                && vpn4GLineRepository.existsByIpAddress(updateRequestDTO.getIpAddress())) {

            throw new ResourceAlreadyExistsException(
                    "IP address "
                            + updateRequestDTO.getIpAddress()
                            + " already exists."
            );
        }

        // Vérification de l'unicité du numéro de série
        if (updateRequestDTO.getSerialNumber() != null
                && !updateRequestDTO.getSerialNumber().equals(vpn4GLine.getSerialNumber())
                && vpn4GLineRepository.existsBySerialNumber(updateRequestDTO.getSerialNumber())) {

            throw new ResourceAlreadyExistsException(
                    "Serial number "
                            + updateRequestDTO.getSerialNumber()
                            + " already exists."
            );
        }

        // Mise à jour des champs simples
        vpn4GLineMapper.updateEntityFromDto(updateRequestDTO, vpn4GLine);

        // Mise à jour des relations
        if (updateRequestDTO.getAgencyId() != null) {
            var agency = agencyRepository.findById(updateRequestDTO.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Agency with id " + updateRequestDTO.getAgencyId() + " not found."
                    ));
            vpn4GLine.setAgency(agency);
        }

        if (updateRequestDTO.getContractId() != null) {
            var contract = contractRepository.findById(updateRequestDTO.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract with id " + updateRequestDTO.getContractId() + " not found."
                    ));
            vpn4GLine.setContract(contract);
        }


        VPN4GLine updatedLine = vpn4GLineRepository.save(vpn4GLine);

        return vpn4GLineMapper.toVPN4GLineResponseDTO(updatedLine);
    }

    public void deleteVPN4GLine(UUID id) {

        VPN4GLine vpn4GLine = vpn4GLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VPN 4G Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(vpn4GLine.getLineStatus())) {
            throw new IllegalStateException(
                    "Cannot delete an active VPN 4G Line."
            );
        }

        vpn4GLineRepository.delete(vpn4GLine);
    }

    public List<VPN4GLineResponseDTO> getAllVPN4GLines() {
        return vpn4GLineRepository.findAll().stream()
            .map(vpn4GLineMapper::toVPN4GLineResponseDTO)
            .collect(Collectors.toList());
    }

    public VPN4GLineResponseDTO getVPN4GLineById(UUID id) {
        VPN4GLine vpn4GLine = vpn4GLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("VPN 4G Line with id " + id + " not found."));
        return vpn4GLineMapper.toVPN4GLineResponseDTO(vpn4GLine);
    }

    public VPN4GLineResponseDTO getVPN4GLineByLineNumber(String lineNumber) {
        VPN4GLine vpn4GLine = vpn4GLineRepository.findByLineNumber(normalizeGsmLikeLineNumber(lineNumber))
            .orElseThrow(() -> new ResourceNotFoundException("VPN 4G Line with number " + lineNumber + " not found."));
        return vpn4GLineMapper.toVPN4GLineResponseDTO(vpn4GLine);
    }
    public List<VPN4GLineResponseDTO> getAllVPN4GLinesByStatus(LineStatus lineStatus) {
        return vpn4GLineRepository.findByLineStatus(lineStatus).stream()
                .map(vpn4GLineMapper::toVPN4GLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VPN4GLineResponseDTO> getAllBillableVPN4GLines(){
        List<VPN4GLineResponseDTO> vpn4GLines = getAllVPN4GLinesByStatus(LineStatus.ACTIVE);
        vpn4GLines.addAll(getAllVPN4GLinesByStatus(LineStatus.SUSPENDED));
        return vpn4GLines;
    }

    public List<VPN4GLineResponseDTO> searchVPN4GLines(String lineNumber, LineStatus lineStatus,
                                                       String equipment, String ipAddress, String serialNumber,
                                                       java.time.LocalDate deliveryDateFrom, java.time.LocalDate deliveryDateTo) {
        Specification<VPN4GLine> spec = LineSpecification.<VPN4GLine>hasLineNumber(
                        lineNumber == null ? null : normalizeGsmLikeLineNumber(lineNumber)
                )
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(VPN4GLineSpecification.hasEquipment(equipment))
                .and(VPN4GLineSpecification.hasIpAddress(ipAddress))
                .and(VPN4GLineSpecification.hasSerialNumber(serialNumber))
                .and(VPN4GLineSpecification.deliveryDateFrom(deliveryDateFrom))
                .and(VPN4GLineSpecification.deliveryDateTo(deliveryDateTo));

        return vpn4GLineRepository.findAll(spec).stream()
                .map(vpn4GLineMapper::toVPN4GLineResponseDTO)
                .collect(Collectors.toList());
    }

    public void terminatedVPN4GLine(UUID id) {

        VPN4GLine vpn4GLine = vpn4GLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VPN 4G Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(vpn4GLine.getLineStatus())) {
            vpn4GLine.setLineStatus(LineStatus.TERMINATED);
            vpn4GLineRepository.save(vpn4GLine);
        }
    }

    private String normalizeGsmLikeLineNumber(String lineNumber) {
        return LineValueUtils.normalizeMoroccanPhoneNumber(lineNumber, '6');
    }
}
