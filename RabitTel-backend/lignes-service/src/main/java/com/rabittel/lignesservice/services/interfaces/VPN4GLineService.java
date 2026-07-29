package com.rabittel.lignesservice.services.interfaces;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.VPN4GLineRequestDTO.*;
import com.rabittel.lignesservice.dtos.response.VPN4GLineResponseDTO;
import com.rabittel.lignesservice.enums.LineStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VPN4GLineService {

    VPN4GLineResponseDTO createVPN4GLine(VPN4GLineCreateRequestDTO dto);

    VPN4GLineResponseDTO updateVPN4GLine(UUID id, VPN4GLineUpdateRequestDTO dto);

    void terminatedVPN4GLine(UUID id);

    void deleteVPN4GLine(UUID id);

    VPN4GLineResponseDTO getVPN4GLineById(UUID id);

    VPN4GLineResponseDTO getVPN4GLineByLineNumber(String lineNumber);

    List<VPN4GLineResponseDTO> getAllVPN4GLines();

    List<VPN4GLineResponseDTO> getAllVPN4GLinesByStatus(LineStatus status);

    List<VPN4GLineResponseDTO> getAllBillableVPN4GLines();

    List<VPN4GLineResponseDTO> searchVPN4GLines(
            String lineNumber,
            LineStatus status,
            String equipment,
            String ipAddress,
            String serialNumber,
            LocalDate deliveryDateFrom,
            LocalDate deliveryDateTo
    );
}