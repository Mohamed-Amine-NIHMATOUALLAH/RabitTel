package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.DataLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.DataLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;

import java.util.List;
import java.util.UUID;

public interface DataLineService {

    DataLineResponseDTO createDataLine(DataLineCreateRequestDTO dto);

    DataLineResponseDTO updateDataLine(UUID id, DataLineUpdateRequestDTO dto);

    void terminateDataLine(UUID id);

    void deleteDataLine(UUID id);

    DataLineResponseDTO getDataLineById(UUID id);

    DataLineResponseDTO getDataLineByLineNumber(String lineNumber);

    List<DataLineResponseDTO> getAllDataLines(LineType lineType);

    List<DataLineResponseDTO> getAllDataLinesByStatus(LineStatus status, LineType lineType);

    List<DataLineResponseDTO> getAllBillableDataLines(LineType lineType);

    List<DataLineResponseDTO> searchDataLines(
            String lineNumber,
            LineStatus status,
            LineType lineType,
            String bandwidth,
            String ipAddress
    );
}