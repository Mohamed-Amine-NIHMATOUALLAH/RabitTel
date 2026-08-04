package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.DataLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.DataLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.DataLineResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.entities.DataLine;
import com.rabittel.lignesservice.enums.ADSLBandwidth;
import com.rabittel.lignesservice.enums.DedicatedLineBandwidth;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.DataLineMapper;
import com.rabittel.lignesservice.repositories.DataLineRepository;
import com.rabittel.lignesservice.services.interfaces.DataLineService;
import com.rabittel.lignesservice.specifications.LineSpecification;
import com.rabittel.lignesservice.specifications.DataLineSpecification;
import com.rabittel.lignesservice.validation.LineValueUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataLineServiceImpl implements DataLineService {
    private static final EnumSet<LineType> DATA_LINE_TYPES = EnumSet.of(
            LineType.VPN_ADSL,
            LineType.ADSL,
            LineType.LLI,
            LineType.VPN_LL
    );

    private final DataLineRepository dataLineRepository;
    private final DataLineMapper dataLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public DataLineResponseDTO createDataLine(DataLineCreateRequestDTO createRequestDTO) {
        validateDataLineType(createRequestDTO.getLineType());
        String normalizedLineNumber = normalizeDataLineNumber(createRequestDTO.getLineNumber(), createRequestDTO.getLineType());
        String normalizedBandwidth = normalizeDataLineBandwidth(createRequestDTO.getBandwidth(), createRequestDTO.getLineType());
        createRequestDTO.setLineNumber(normalizedLineNumber);
        createRequestDTO.setBandwidth(normalizedBandwidth);

        if (dataLineRepository.existsByLineNumber(normalizedLineNumber)) {
            throw new ResourceAlreadyExistsException("Data Line with number " + normalizedLineNumber + " already exists.");
        }

        if (dataLineRepository.existsByIpAddress(createRequestDTO.getIpAddress())) {
            throw new ResourceAlreadyExistsException("IP address " + createRequestDTO.getIpAddress() + " already exists.");
        }

        DataLine dataLine = dataLineMapper.toEntity(createRequestDTO);

        Agency agency = agencyRepository.findById(createRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        dataLine.setAgency(agency);

        dataLine.setLineStatus(LineStatus.ACTIVE);


        DataLine savedLine = dataLineRepository.save(dataLine);
        return dataLineMapper.toDataLineResponseDTO(savedLine);
    }

    public DataLineResponseDTO updateDataLine(UUID id, DataLineUpdateRequestDTO updateRequestDTO) {
        if (updateRequestDTO.getLineType() != null) {
            validateDataLineType(updateRequestDTO.getLineType());
        }

        DataLine dataLine = dataLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Data Line with id " + id + " not found."
                ));

        LineType effectiveType =
                updateRequestDTO.getLineType() != null
                        ? updateRequestDTO.getLineType()
                        : dataLine.getLineType();

        if (updateRequestDTO.getLineNumber() != null) {
            updateRequestDTO.setLineNumber(normalizeDataLineNumber(updateRequestDTO.getLineNumber(), effectiveType));
        }

        if (updateRequestDTO.getBandwidth() != null) {
            updateRequestDTO.setBandwidth(normalizeDataLineBandwidth(updateRequestDTO.getBandwidth(), effectiveType));
        }

        // Vérification de l'unicité du numéro de ligne
        if (updateRequestDTO.getLineNumber() != null
                && !updateRequestDTO.getLineNumber().equals(dataLine.getLineNumber())
                && dataLineRepository.existsByLineNumber(updateRequestDTO.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "Data Line with number "
                            + updateRequestDTO.getLineNumber()
                            + " already exists."
            );
        }

        // Vérification de l'unicité de l'adresse IP
        if (updateRequestDTO.getIpAddress() != null
                && !updateRequestDTO.getIpAddress().equals(dataLine.getIpAddress())
                && dataLineRepository.existsByIpAddress(updateRequestDTO.getIpAddress())) {

            throw new ResourceAlreadyExistsException(
                    "IP address "
                            + updateRequestDTO.getIpAddress()
                            + " already exists."
            );
        }

        // Mise à jour des champs simples
        dataLineMapper.updateEntityFromDto(updateRequestDTO, dataLine);

        // Mise à jour des relations
        if (updateRequestDTO.getAgencyId() != null) {
            var agency = agencyRepository.findById(updateRequestDTO.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Agency with id " + updateRequestDTO.getAgencyId() + " not found."
                    ));
            dataLine.setAgency(agency);
        }

        if (updateRequestDTO.getContractId() != null) {
            var contract = contractRepository.findById(updateRequestDTO.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract with id " + updateRequestDTO.getContractId() + " not found."
                    ));
            dataLine.setContract(contract);
        }


        DataLine updatedLine = dataLineRepository.save(dataLine);

        return dataLineMapper.toDataLineResponseDTO(updatedLine);
    }


    public void terminateDataLine(UUID id) {

        DataLine dataLine = dataLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Data Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(dataLine.getLineStatus())) {
            dataLine.setLineStatus(LineStatus.TERMINATED);
            dataLineRepository.save(dataLine);
        }
    }


    public void deleteDataLine(UUID id) {

        DataLine dataLine = dataLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Data Line with id " + id + " not found."
                ));

        if (LineStatus.ACTIVE.equals(dataLine.getLineStatus())) {
            throw new IllegalStateException(
                    "Cannot delete an active Data Line."
            );
        }

        dataLineRepository.delete(dataLine);
    }

    public List<DataLineResponseDTO> getAllDataLines(LineType lineType) {
        if (lineType != null) {
            validateDataLineType(lineType);
        }
        Specification<DataLine> spec = LineSpecification.<DataLine>hasLineType(lineType);

        return dataLineRepository.findAll(spec).stream()
            .map(dataLineMapper::toDataLineResponseDTO)
            .collect(Collectors.toList());
    }

    public DataLineResponseDTO getDataLineById(UUID id) {
        DataLine dataLine = dataLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Data Line with id " + id + " not found."));
        return dataLineMapper.toDataLineResponseDTO(dataLine);
    }

    public DataLineResponseDTO getDataLineByLineNumber(String lineNumber) {
        String normalizedLineNumber = normalizeAnyDataLineNumber(lineNumber);
        DataLine dataLine = dataLineRepository.findByLineNumber(normalizedLineNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Data Line with number " + lineNumber + " not found."));
        return dataLineMapper.toDataLineResponseDTO(dataLine);
    }

    public List<DataLineResponseDTO> getAllDataLinesByStatus(LineStatus lineStatus, LineType lineType) {
        if (lineType != null) {
            validateDataLineType(lineType);
        }
        Specification<DataLine> spec = LineSpecification.<DataLine>hasLineStatus(lineStatus)
                .and(LineSpecification.hasLineType(lineType));

        return dataLineRepository.findAll(spec).stream()
                .map(dataLineMapper::toDataLineResponseDTO)
                .collect(Collectors.toList());
    }

    public List<DataLineResponseDTO> getAllBillableDataLines(LineType lineType){
        List<DataLineResponseDTO> dataLines = getAllDataLinesByStatus(LineStatus.ACTIVE, lineType);
        dataLines.addAll(getAllDataLinesByStatus(LineStatus.SUSPENDED, lineType));
        return dataLines;
    }

    public List<DataLineResponseDTO> searchDataLines(
            String lineNumber,
            LineStatus lineStatus,
            LineType lineType,
            String bandwidth,
            String ipAddress) {
        if (lineType != null) {
            validateDataLineType(lineType);
        }

        String normalizedLineNumber = lineNumber == null ? null : normalizeAnyDataLineNumber(lineNumber);
        String normalizedBandwidth = bandwidth == null ? null : bandwidth.trim();

        Specification<DataLine> spec = LineSpecification.<DataLine>hasLineNumber(normalizedLineNumber)
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(LineSpecification.hasLineType(lineType))
                .and(DataLineSpecification.hasBandwidth(normalizedBandwidth))
                .and(DataLineSpecification.hasIpAddress(ipAddress));

        return dataLineRepository.findAll(spec).stream()
                .map(dataLineMapper::toDataLineResponseDTO)
                .collect(Collectors.toList());
    }

    private void validateDataLineType(LineType lineType) {
        if (!DATA_LINE_TYPES.contains(lineType)) {
            throw new IllegalArgumentException("Unsupported line type for data lines: " + lineType);
        }
    }

    private String normalizeDataLineNumber(String lineNumber, LineType lineType) {
        if (lineType == LineType.VPN_ADSL || lineType == LineType.ADSL) {
            return LineValueUtils.normalizeMoroccanPhoneNumber(lineNumber, '5');
        }

        if (lineType == LineType.LLI || lineType == LineType.VPN_LL) {
            if (!LineValueUtils.isAlphanumeric(lineNumber)) {
                throw new IllegalArgumentException("Line number must be alphanumeric for " + lineType);
            }
            return lineNumber.trim();
        }

        throw new IllegalArgumentException("Unsupported data line type: " + lineType);
    }

    private String normalizeAnyDataLineNumber(String lineNumber) {
        try {
            return LineValueUtils.normalizeMoroccanPhoneNumber(lineNumber, '6');
        } catch (IllegalArgumentException ex6) {
            try {
                return LineValueUtils.normalizeMoroccanPhoneNumber(lineNumber, '5');
            } catch (IllegalArgumentException ex5) {
                if (LineValueUtils.isAlphanumeric(lineNumber)) {
                    return lineNumber.trim();
                }
                throw new IllegalArgumentException("Invalid line number format");
            }
        }
    }

    private String normalizeDataLineBandwidth(String bandwidth, LineType lineType) {
        if (bandwidth == null) {
            return null;
        }

        if (lineType == LineType.LLI || lineType == LineType.VPN_LL) {
            return DedicatedLineBandwidth.fromValue(bandwidth).getLabel();
        }

        if (lineType == LineType.VPN_ADSL || lineType == LineType.ADSL) {
            return ADSLBandwidth.fromValue(bandwidth).getLabel();
        }

        throw new IllegalArgumentException("Unsupported data line type: " + lineType);
    }
}
