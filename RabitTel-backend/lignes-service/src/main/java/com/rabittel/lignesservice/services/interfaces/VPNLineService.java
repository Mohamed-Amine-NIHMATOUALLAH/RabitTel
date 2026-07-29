package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPNLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.VPNLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;

import java.util.List;
import java.util.UUID;

public interface VPNLineService {

    VPNLineResponseDTO createVPNLine(VPNLineCreateRequestDTO dto);

    VPNLineResponseDTO updateVPNLine(UUID id, VPNLineUpdateRequestDTO dto);

    void terminatedVPNLine(UUID id);

    void deleteVPNLine(UUID id);

    VPNLineResponseDTO getVPNLineById(UUID id);

    VPNLineResponseDTO getVPNLineByLineNumber(String lineNumber);

    List<VPNLineResponseDTO> getAllVPNLines();

    List<VPNLineResponseDTO> getAllVPNLinesByStatus(LineStatus status);

    List<VPNLineResponseDTO> getAllBillableVPNLines();

    List<VPNLineResponseDTO> searchVPNLines(
            String lineNumber,
            LineStatus status,
            String bandwidth,
            String ipAddress
    );
}