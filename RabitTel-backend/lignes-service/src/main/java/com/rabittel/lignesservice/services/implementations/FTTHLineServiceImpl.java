package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.FTTHLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.FTTHLineResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.entities.FTTHLine;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.FTTHLineMapper;
import com.rabittel.lignesservice.repositories.FTTHLineRepository;
import com.rabittel.lignesservice.services.interfaces.FTTHLineService;
import com.rabittel.lignesservice.specifications.FTTHLineSpecification;
import com.rabittel.lignesservice.specifications.LineSpecification;
import com.rabittel.lignesservice.validation.LineValueUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FTTHLineServiceImpl implements FTTHLineService {
    private final FTTHLineRepository ftthLineRepository;
    private final FTTHLineMapper ftthLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public FTTHLineResponseDTO createFTTHLine(FTTHLineCreateRequestDTO createRequestDTO) {
        String normalizedLineNumber = normalizeFixedLineNumber(createRequestDTO.getLineNumber());
        String normalizedFixedLineNumber = normalizeFixedLineNumber(createRequestDTO.getFixedLineNumber());
        createRequestDTO.setLineNumber(normalizedLineNumber);
        createRequestDTO.setFixedLineNumber(normalizedFixedLineNumber);

        if (ftthLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("FTTH Line with number " + createRequestDTO.getLineNumber() + " already exists.");
        }

        if (ftthLineRepository.existsByFixedLineNumber(createRequestDTO.getFixedLineNumber())) {
            throw new ResourceAlreadyExistsException("Fixed line number " + createRequestDTO.getFixedLineNumber() + " already exists.");
        }

        FTTHLine ftthLine = ftthLineMapper.toEntity(createRequestDTO);

        Agency agency = agencyRepository.findById(createRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        ftthLine.setAgency(agency);
        ftthLine.setLineType(LineType.FTTH);
        ftthLine.setLineStatus(LineStatus.ACTIVE);


        FTTHLine savedLine = ftthLineRepository.save(ftthLine);

        return ftthLineMapper.toFTTHLineResponseDTO(savedLine);
    }

    public FTTHLineResponseDTO updateFTTHLine(UUID id, FTTHLineUpdateRequestDTO dto) {

        FTTHLine ftthLine = ftthLineRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("FTTH Line not found")
                );

        if (dto.getLineNumber() != null) {
            dto.setLineNumber(normalizeFixedLineNumber(dto.getLineNumber()));
        }
        if (dto.getFixedLineNumber() != null) {
            dto.setFixedLineNumber(normalizeFixedLineNumber(dto.getFixedLineNumber()));
        }

        if(dto.getLineNumber()!=null
                && !dto.getLineNumber().equals(ftthLine.getLineNumber())
                && ftthLineRepository.existsByLineNumber(dto.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "Line number already exists"
            );
        }


        if(dto.getFixedLineNumber()!=null
                && !dto.getFixedLineNumber().equals(ftthLine.getFixedLineNumber())
                && ftthLineRepository.existsByFixedLineNumber(dto.getFixedLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "Fixed number already exists"
            );
        }

        ftthLineMapper.updateEntityFromDto(dto, ftthLine);

        if (dto.getAgencyId() != null) {
            Agency agency = agencyRepository.findById(dto.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
            ftthLine.setAgency(agency);
        }
        if (dto.getContractId() != null) {
            var contract = contractRepository.findById(dto.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
            ftthLine.setContract(contract);
        }

        return ftthLineMapper.toFTTHLineResponseDTO(
                ftthLineRepository.save(ftthLine)
        );
    }

    public void terminatedFTTHLine(UUID id) {
        FTTHLine ftthLine = ftthLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with id " + id + " not found."));
        if(LineStatus.ACTIVE.equals(ftthLine.getLineStatus())){
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
        String normalized = normalizeFixedLineNumber(lineNumber);
        FTTHLine ftthLine = ftthLineRepository.findByLineNumber(normalized)
            .orElseThrow(() -> new ResourceNotFoundException("FTTH Line with number " + lineNumber + " not found."));
        return ftthLineMapper.toFTTHLineResponseDTO(ftthLine);
    }

    public List<FTTHLineResponseDTO> getAllFTTHLinesByStatus(LineStatus lineStatus) {
        return ftthLineRepository.findByLineStatus(lineStatus).stream()
                .map(ftthLineMapper::toFTTHLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FTTHLineResponseDTO> getAllBillableFTTHLines(){
        List<LineStatus> statuses =
                List.of(
                        LineStatus.ACTIVE,
                        LineStatus.SUSPENDED
                );
        List<FTTHLine> ftthLines = ftthLineRepository.findByLineStatusIn(statuses);
        return ftthLines.stream()
                .map(ftthLineMapper::toFTTHLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FTTHLineResponseDTO> searchFTTHLines(String lineNumber, LineStatus lineStatus,
                                                     String fixedLineNumber, String routerBrand, String bandwidth) {
        String normalizedLineNumber = lineNumber == null ? null : normalizeFixedLineNumber(lineNumber);
        String normalizedFixedLineNumber = fixedLineNumber == null ? null : normalizeFixedLineNumber(fixedLineNumber);
        Specification<FTTHLine> spec = LineSpecification.<FTTHLine>hasLineNumber(normalizedLineNumber)
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(FTTHLineSpecification.hasFixedLineNumber(normalizedFixedLineNumber))
                .and(FTTHLineSpecification.hasRouterBrand(routerBrand))
                .and(FTTHLineSpecification.hasBandwidth(bandwidth));

        return ftthLineRepository.findAll(spec).stream()
                .map(ftthLineMapper::toFTTHLineResponseDTO)
                .collect(Collectors.toList());
    }

    private String normalizeFixedLineNumber(String value) {
        return LineValueUtils.normalizeMoroccanPhoneNumber(value, '5');
    }
}
