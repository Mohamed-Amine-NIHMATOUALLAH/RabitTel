package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.FTTHLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.FTTHLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;

import java.util.List;
import java.util.UUID;

public interface FTTHLineService {

    FTTHLineResponseDTO createFTTHLine(FTTHLineCreateRequestDTO dto);

    FTTHLineResponseDTO updateFTTHLine(UUID id, FTTHLineUpdateRequestDTO dto);

    void terminatedFTTHLine(UUID id);

    void deleteFTTHLine(UUID id);

    FTTHLineResponseDTO getFTTHLineById(UUID id);

    FTTHLineResponseDTO getFTTHLineByLineNumber(String lineNumber);

    List<FTTHLineResponseDTO> getAllFTTHLines();


    List<FTTHLineResponseDTO> getAllBillableFTTHLines();

    List<FTTHLineResponseDTO> searchFTTHLines(String lineNumber, LineStatus lineStatus,
                                              String fixedLineNumber, String routerBrand, Long bandwidth);
}