package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.GSMLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;

import java.util.List;
import java.util.UUID;

public interface GSMLineService {

    GSMLineResponseDTO createGSMLine(GSMLineCreateRequestDTO dto);

    GSMLineResponseDTO updateGSMLine(UUID id, GSMLineUpdateRequestDTO dto);

    void terminatedGSMLine(UUID id);

    void deleteGSMLine(UUID id);

    GSMLineResponseDTO getGSMLineById(UUID id);

    GSMLineResponseDTO getGSMLineByLineNumber(String lineNumber);

    List<GSMLineResponseDTO> getAllGSMLines();

    List<GSMLineResponseDTO> getAllGSMLinesByStatus(LineStatus lineStatus);

    List<GSMLineResponseDTO> getAllBillableGSMLines();

    public List<GSMLineResponseDTO> searchGSMLines(String lineNumber, LineStatus lineStatus,
                                                   String serviceFunction, String chipSerialNumber,
                                                   java.time.LocalDate chipDeliveryDateFrom, java.time.LocalDate chipDeliveryDateTo,
                                                   String pinCode, String pukCode);
}