package com.rabittel.lignesservice.services;

import com.rabittel.lignesservice.dtos.response.LineResponseDTO;
import com.rabittel.lignesservice.entities.Line;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.LineMapper;
import com.rabittel.lignesservice.repositories.LineRepository;
import com.rabittel.lignesservice.specifications.LineSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Vue transverse en lecture seule sur toutes les lignes, tous types confondus.
 * La création/modification reste la responsabilité de chaque service spécialisé
 * (GSMLineService, FTTHLineService, ...).
 */
@Service
@AllArgsConstructor
public class LineService {

    private final LineRepository lineRepository;
    private final LineMapper lineMapper;

    public List<LineResponseDTO> getAllLines() {
        return lineRepository.findAll().stream()
                .map(lineMapper::toLineResponseDTO)
                .collect(Collectors.toList());
    }

    public LineResponseDTO getLineById(UUID id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line with id " + id + " not found."));
        return lineMapper.toLineResponseDTO(line);
    }

    public List<LineResponseDTO> getAllBillableLines() {
        List<LineResponseDTO> billable = searchLines(null, LineStatus.ACTIVE, null, null, null);
        billable.addAll(searchLines(null, LineStatus.SUSPENDED, null, null, null));
        return billable;
    }

    public List<LineResponseDTO> searchLines(String lineNumber, LineStatus lineStatus, LineType lineType,
                                             UUID agencyId, UUID planId) {
        Specification<Line> spec = Specification
                .<Line>where(LineSpecification.hasLineNumber(lineNumber))
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(LineSpecification.hasLineType(lineType))
                .and(LineSpecification.hasAgencyId(agencyId))
                .and(LineSpecification.hasPlanId(planId));

        return lineRepository.findAll(spec).stream()
                .map(lineMapper::toLineResponseDTO)
                .collect(Collectors.toList());
    }
}