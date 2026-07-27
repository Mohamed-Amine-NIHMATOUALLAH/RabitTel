package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.FTTHLineResponseDTO;
import com.rabittel.lignesservice.dtos.response.GSMLineResponseDTO;
import com.rabittel.lignesservice.entities.FTTHLine;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.FTTHLineMapper;
import com.rabittel.lignesservice.repositories.FTTHLineRepository;
import com.rabittel.lignesservice.specifications.FTTHLineSpecification;
import com.rabittel.lignesservice.specifications.LineSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FTTHLineService {
    private final FTTHLineRepository ftthLineRepository;
    private final FTTHLineMapper ftthLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.PlanRepository planRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public FTTHLineResponseDTO createFTTHLine(FTTHLineCreateRequestDTO createRequestDTO) {
        if (ftthLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("FTTH Line with number " + createRequestDTO.getLineNumber() + " already exists.");
        }

        if (ftthLineRepository.existsByFixedLineNumber(createRequestDTO.getFixedLineNumber())) {
            throw new ResourceAlreadyExistsException("Fixed line number " + createRequestDTO.getFixedLineNumber() + " already exists.");
        }

        FTTHLine ftthLine = ftthLineMapper.toEntity(createRequestDTO);
        FTTHLine savedLine = ftthLineRepository.save(ftthLine);
        return ftthLineMapper.toFTTHLineResponseDTO(savedLine);
    }

    public FTTHLineResponseDTO updateFTTHLine(UUID id, FTTHLineUpdateRequestDTO updateRequestDTO) {
        FTTHLine ftthLine = ftthLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with id " + id + " not found."));

        ftthLineMapper.updateEntityFromDto(updateRequestDTO, ftthLine);

        if (updateRequestDTO.getLineNumber() != null && !updateRequestDTO.getLineNumber().equals(ftthLine.getLineNumber())
            && ftthLineRepository.existsByLineNumber(updateRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("FTTH Line with number " + updateRequestDTO.getLineNumber() + " already exists.");
        }

        if (updateRequestDTO.getFixedLineNumber() != null && !updateRequestDTO.getFixedLineNumber().equals(ftthLine.getFixedLineNumber())
            && ftthLineRepository.existsByFixedLineNumber(updateRequestDTO.getFixedLineNumber())) {
            throw new ResourceAlreadyExistsException("Fixed line number " + updateRequestDTO.getFixedLineNumber() + " already exists.");
        }

        if (updateRequestDTO.getAgencyId() != null) {
            var agency = agencyRepository.findById(updateRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + updateRequestDTO.getAgencyId() + " not found."));
            ftthLine.setAgency(agency);
        }
        if (updateRequestDTO.getPlanId() != null) {
            var plan = planRepository.findById(updateRequestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan with id " + updateRequestDTO.getPlanId() + " not found."));
            ftthLine.setPlan(plan);
        }
        if (updateRequestDTO.getContractId() != null) {
            var contract = contractRepository.findById(updateRequestDTO.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + updateRequestDTO.getContractId() + " not found."));
            ftthLine.setContract(contract);
        }
        if (updateRequestDTO.getCreatedBy() != null) {
            ftthLine.setCreatedBy(updateRequestDTO.getCreatedBy());
        }

        FTTHLine updatedLine = ftthLineRepository.save(ftthLine);
        return ftthLineMapper.toFTTHLineResponseDTO(updatedLine);
    }

    public void terminatedFTTHLine(UUID id) {
        FTTHLine ftthLine = ftthLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with id " + id + " not found."));
        if (ftthLine.getLineStatus() != null && ftthLine.getLineStatus().equals(LineStatus.ACTIVE)) {
            ftthLine.setLineStatus(LineStatus.TERMINATED);
            ftthLineRepository.save(ftthLine);
        }

    }

    //Admin
    public void deleteFTTHLine(UUID id) {
        FTTHLine ftthLine = ftthLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with id " + id + " not found."));
        if (ftthLine.getLineStatus() != null && ftthLine.getLineStatus().equals(LineStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot delete an active FTTH Line.");
        }
        ftthLineRepository.delete(ftthLine);
    }

    public List<FTTHLineResponseDTO> getAllFTTHLines() {
        return ftthLineRepository.findAll().stream()
            .map(ftthLineMapper::toFTTHLineResponseDTO)
            .collect(Collectors.toList());
    }

    public FTTHLineResponseDTO getFTTHLineById(UUID id) {
        FTTHLine ftthLine = ftthLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with id " + id + " not found."));
        return ftthLineMapper.toFTTHLineResponseDTO(ftthLine);
    }

    public FTTHLineResponseDTO getFTTHLineByLineNumber(String lineNumber) {
        FTTHLine ftthLine = ftthLineRepository.findByLineNumber(lineNumber)
            .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with number " + lineNumber + " not found."));
        return ftthLineMapper.toFTTHLineResponseDTO(ftthLine);
    }

    public List<FTTHLineResponseDTO> getAllFTTHLinesByStatus(LineStatus lineStatus) {
        return ftthLineRepository.findByLineStatus(lineStatus).stream()
                .map(ftthLineMapper::toFTTHLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FTTHLineResponseDTO> getAllBillableFTTHLines(){
        List<FTTHLineResponseDTO> ftthLines = getAllFTTHLinesByStatus(LineStatus.ACTIVE);
        ftthLines.addAll(getAllFTTHLinesByStatus(LineStatus.SUSPENDED));
        return ftthLines;
    }

    public List<FTTHLineResponseDTO> searchFTTHLines(String lineNumber, LineStatus lineStatus,
                                                     String fixedLineNumber, String routerBrand, String bandwidth) {
        Specification<FTTHLine> spec = Specification
                .<FTTHLine>where(LineSpecification.hasLineNumber(lineNumber))
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(FTTHLineSpecification.hasFixedLineNumber(fixedLineNumber))
                .and(FTTHLineSpecification.hasRouterBrand(routerBrand))
                .and(FTTHLineSpecification.hasBandwidth(bandwidth));

        return ftthLineRepository.findAll(spec).stream()
                .map(ftthLineMapper::toFTTHLineResponseDTO)
                .collect(Collectors.toList());
    }
}
