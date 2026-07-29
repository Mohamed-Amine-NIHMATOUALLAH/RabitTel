package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.RTCLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.RTCLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;

import java.util.List;
import java.util.UUID;

public interface RTCLineService {

    RTCLineResponseDTO createRTCLine(RTCLineCreateRequestDTO dto);

    RTCLineResponseDTO updateRTCLine(UUID id, RTCLineUpdateRequestDTO dto);

    void terminatedRTCLine(UUID id);

    void deleteRTCLine(UUID id);

    RTCLineResponseDTO getRTCLineById(UUID id);

    RTCLineResponseDTO getRTCLineByLineNumber(String lineNumber);

    List<RTCLineResponseDTO> getAllRTCLines();

    List<RTCLineResponseDTO> getAllRTCLinesByStatus(LineStatus status);

    List<RTCLineResponseDTO> getAllBillableRTCLines();

    List<RTCLineResponseDTO> searchRTCLines(String lineNumber, LineStatus status);
}