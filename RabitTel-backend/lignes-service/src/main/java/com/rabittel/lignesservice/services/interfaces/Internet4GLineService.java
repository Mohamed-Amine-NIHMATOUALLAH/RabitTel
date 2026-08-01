package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.Internet4GLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;

import java.util.List;
import java.util.UUID;

public interface Internet4GLineService {

    Internet4GLineResponseDTO createInternet4GLine(Internet4GLineCreateRequestDTO dto);

    Internet4GLineResponseDTO updateInternet4GLine(UUID id, Internet4GLineUpdateRequestDTO dto);

    void terminatedInternet4GLine(UUID id);

    void deleteInternet4GLine(UUID id);

    Internet4GLineResponseDTO getInternet4GLineById(UUID id);

    Internet4GLineResponseDTO getInternet4GLineByLineNumber(String lineNumber);

    List<Internet4GLineResponseDTO> getAllInternet4GLines();

    List<Internet4GLineResponseDTO> getAllInternet4GLinesByStatus(LineStatus status);

    List<Internet4GLineResponseDTO> getAllBillableInternet4GLines();

    List<Internet4GLineResponseDTO> searchInternet4GLines(
            String lineNumber,
            LineStatus lineStatus,
            String serviceFunction,
            String simSerialNumber,
            String pinCode,
            String pukCode,
            String equipment,
            String equipmentSerialNumber,
            Long bandwidth);
}