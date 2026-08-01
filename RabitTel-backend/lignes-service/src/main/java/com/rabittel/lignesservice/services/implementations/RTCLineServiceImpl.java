package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.RTCLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.RTCLineResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.entities.Plan;
import com.rabittel.lignesservice.entities.RTCLine;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.RTCLineMapper;
import com.rabittel.lignesservice.repositories.RTCLineRepository;
import com.rabittel.lignesservice.services.interfaces.RTCLineService;
import com.rabittel.lignesservice.specifications.LineSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RTCLineServiceImpl implements RTCLineService {
    private final RTCLineRepository rtcLineRepository;
    private final RTCLineMapper rtcLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.PlanRepository planRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public RTCLineResponseDTO createRTCLine(RTCLineCreateRequestDTO createRequestDTO) {
        if (rtcLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("RTC Line with number " + createRequestDTO.getLineNumber() + " already exists.");
        }

        RTCLine rtcLine = rtcLineMapper.toEntity(createRequestDTO);

        Agency agency = agencyRepository.findById(createRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
        Plan plan = planRepository.findById(createRequestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        rtcLine.setAgency(agency);
        rtcLine.setPlan(plan);

        rtcLine.setLineType(LineType.RTC);
        rtcLine.setLineStatus(LineStatus.ACTIVE);


        RTCLine savedLine = rtcLineRepository.save(rtcLine);

        return rtcLineMapper.toRTCLineResponseDTO(savedLine);
    }

    public RTCLineResponseDTO updateRTCLine(UUID id, RTCLineUpdateRequestDTO updateRequestDTO) {

        RTCLine rtcLine = rtcLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RTC Line with id " + id + " not found."
                ));

        // Vérification de l'unicité du numéro avant le mapper
        if (updateRequestDTO.getLineNumber() != null
                && !updateRequestDTO.getLineNumber().equals(rtcLine.getLineNumber())
                && rtcLineRepository.existsByLineNumber(updateRequestDTO.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "RTC Line with number "
                            + updateRequestDTO.getLineNumber()
                            + " already exists."
            );
        }

        // Mise à jour des champs simples
        rtcLineMapper.updateEntityFromDto(updateRequestDTO, rtcLine);

        // Mise à jour des relations
        if (updateRequestDTO.getAgencyId() != null) {
            var agency = agencyRepository.findById(updateRequestDTO.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Agency with id " + updateRequestDTO.getAgencyId() + " not found."
                    ));
            rtcLine.setAgency(agency);
        }

        if (updateRequestDTO.getPlanId() != null) {
            var plan = planRepository.findById(updateRequestDTO.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Plan with id " + updateRequestDTO.getPlanId() + " not found."
                    ));
            rtcLine.setPlan(plan);
        }

        if (updateRequestDTO.getContractId() != null) {
            var contract = contractRepository.findById(updateRequestDTO.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract with id " + updateRequestDTO.getContractId() + " not found."
                    ));
            rtcLine.setContract(contract);
        }


        RTCLine updatedLine = rtcLineRepository.save(rtcLine);

        return rtcLineMapper.toRTCLineResponseDTO(updatedLine);
    }


    public void terminatedRTCLine(UUID id) {

        RTCLine rtcLine = rtcLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RTC Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(rtcLine.getLineStatus())) {
            rtcLine.setLineStatus(LineStatus.TERMINATED);
            rtcLineRepository.save(rtcLine);
        }
    }

    public void deleteRTCLine(UUID id) {

        RTCLine rtcLine = rtcLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RTC Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(rtcLine.getLineStatus())) {
            throw new IllegalStateException(
                    "Cannot delete an active RTC Line."
            );
        }

        rtcLineRepository.delete(rtcLine);
    }

    public List<RTCLineResponseDTO> getAllRTCLines() {
        return rtcLineRepository.findAll().stream()
            .map(rtcLineMapper::toRTCLineResponseDTO)
            .collect(Collectors.toList());
    }

    public RTCLineResponseDTO getRTCLineById(UUID id) {
        RTCLine rtcLine = rtcLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RTC Line with id " + id + " not found."));
        return rtcLineMapper.toRTCLineResponseDTO(rtcLine);
    }

    public RTCLineResponseDTO getRTCLineByLineNumber(String lineNumber) {
        RTCLine rtcLine = rtcLineRepository.findByLineNumber(lineNumber)
            .orElseThrow(() -> new ResourceNotFoundException("RTC Line with number " + lineNumber + " not found."));
        return rtcLineMapper.toRTCLineResponseDTO(rtcLine);
    }

    public List<RTCLineResponseDTO> getAllRTCLinesByStatus(LineStatus lineStatus) {
        return rtcLineRepository.findByLineStatus(lineStatus).stream()
                .map(rtcLineMapper::toRTCLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RTCLineResponseDTO> getAllBillableRTCLines(){
        List<RTCLineResponseDTO> rtcLines = getAllRTCLinesByStatus(LineStatus.ACTIVE);
        rtcLines.addAll(getAllRTCLinesByStatus(LineStatus.SUSPENDED));
        return rtcLines;
    }

    public List<RTCLineResponseDTO> searchRTCLines(String lineNumber, LineStatus lineStatus) {
        Specification<RTCLine> spec = Specification
                .<RTCLine>where(LineSpecification.hasLineNumber(lineNumber))
                .and(LineSpecification.hasLineStatus(lineStatus));

        return rtcLineRepository.findAll(spec).stream()
                .map(rtcLineMapper::toRTCLineResponseDTO)
                .collect(Collectors.toList());
    }
}
