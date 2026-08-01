package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.GSMLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.GSMLineRequestDTO.GSMLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.GSMLineResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.entities.GSMLine;
import com.rabittel.lignesservice.entities.Plan;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.GSMLineMapper;
import com.rabittel.lignesservice.repositories.GSMLineRepository;
import com.rabittel.lignesservice.services.interfaces.GSMLineService;
import com.rabittel.lignesservice.specifications.GSMLineSpecification;
import com.rabittel.lignesservice.specifications.LineSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GSMLineServiceImpl implements GSMLineService {
    private final GSMLineRepository gsmLineRepository;
    private final GSMLineMapper gsmLineMapper;
    private final com.rabittel.lignesservice.repositories.AgencyRepository agencyRepository;
    private final com.rabittel.lignesservice.repositories.PlanRepository planRepository;
    private final com.rabittel.lignesservice.repositories.ContractRepository contractRepository;

    public GSMLineResponseDTO createGSMLine(GSMLineCreateRequestDTO createRequestDTO) {
        if (gsmLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException("GSM Line with number " + createRequestDTO.getLineNumber() + " already exists.");
        }

        if (gsmLineRepository.existsByChipSerialNumber(createRequestDTO.getChipSerialNumber())) {
            throw new ResourceAlreadyExistsException("Chip serial number " + createRequestDTO.getChipSerialNumber() + " already exists.");
        }

        GSMLine gsmLine = gsmLineMapper.toEntity(createRequestDTO);

        Agency agency = agencyRepository.findById(createRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
        Plan plan = planRepository.findById(createRequestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        gsmLine.setAgency(agency);
        gsmLine.setPlan(plan);

        gsmLine.setLineType(LineType.GSM_PRO);
        gsmLine.setLineStatus(LineStatus.ACTIVE);


        GSMLine savedLine = gsmLineRepository.save(gsmLine);

        return gsmLineMapper.toGSMLineResponseDTO(savedLine);
    }

    public GSMLineResponseDTO updateGSMLine(UUID id, GSMLineUpdateRequestDTO updateRequestDTO) {
        GSMLine gsmLine = gsmLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GSM Line with id " + id + " not found."));


        // check chip serial uniqueness only if new value provided
        if (updateRequestDTO.getChipSerialNumber() != null
            && !updateRequestDTO.getChipSerialNumber().equals(gsmLine.getChipSerialNumber())
            && gsmLineRepository.existsByChipSerialNumber(updateRequestDTO.getChipSerialNumber())) {
            throw new ResourceAlreadyExistsException("Chip serial number " + updateRequestDTO.getChipSerialNumber() + " already exists.");
        }

        if(updateRequestDTO.getLineNumber() != null
                && !updateRequestDTO.getLineNumber()
                .equals(gsmLine.getLineNumber())
                && gsmLineRepository.existsByLineNumber(updateRequestDTO.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "GSM Line with number "
                            + updateRequestDTO.getLineNumber()
                            + " already exists."
            );
        }

        // map simple fields
        gsmLineMapper.updateEntityFromDto(updateRequestDTO, gsmLine);

        // relations: agency, plan, contract
        if (updateRequestDTO.getAgencyId() != null) {
            var agency = agencyRepository.findById(updateRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency with id " + updateRequestDTO.getAgencyId() + " not found."));
            gsmLine.setAgency(agency);
        }
        if (updateRequestDTO.getPlanId() != null) {
            var plan = planRepository.findById(updateRequestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan with id " + updateRequestDTO.getPlanId() + " not found."));
            gsmLine.setPlan(plan);
        }
        if (updateRequestDTO.getContractId() != null) {
            var contract = contractRepository.findById(updateRequestDTO.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + updateRequestDTO.getContractId() + " not found."));
            gsmLine.setContract(contract);
        }



        GSMLine updatedLine = gsmLineRepository.save(gsmLine);
        return gsmLineMapper.toGSMLineResponseDTO(updatedLine);
    }

    public void terminatedGSMLine(UUID id) {
        GSMLine gsmLine = gsmLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GSM Line with id " + id + " not found."));
        if(LineStatus.ACTIVE.equals(gsmLine.getLineStatus())
                || LineStatus.SUSPENDED.equals(gsmLine.getLineStatus())) {

            gsmLine.setLineStatus(LineStatus.TERMINATED);
            gsmLineRepository.save(gsmLine);
        }
    }

    //Admin
    public void deleteGSMLine(UUID id) {
        GSMLine gsmLine = gsmLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GSM Line with id " + id + " not found."));
        if (gsmLine.getLineStatus() != null && gsmLine.getLineStatus().equals(LineStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot delete an active GSM Line.");
        }
        gsmLineRepository.delete(gsmLine);
    }

    public List<GSMLineResponseDTO> getAllGSMLinesByStatus(LineStatus lineStatus) {
        return gsmLineRepository.findByLineStatus(lineStatus).stream()
            .map(gsmLineMapper::toGSMLineResponseDTO)
            .collect(Collectors.toList());
    }

    public List<GSMLineResponseDTO> getAllBillableGSMLines(){

        return gsmLineRepository
                .findByLineStatusIn(
                        List.of(
                                LineStatus.ACTIVE,
                                LineStatus.SUSPENDED
                        )
                )
                .stream()
                .map(gsmLineMapper::toGSMLineResponseDTO)
                .toList();
    }


    public List<GSMLineResponseDTO> getAllGSMLines() {
        return gsmLineRepository.findAll().stream()
            .map(gsmLineMapper::toGSMLineResponseDTO)
            .collect(Collectors.toList());
    }

    public GSMLineResponseDTO getGSMLineById(UUID id) {
        GSMLine gsmLine = gsmLineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GSM Line with id " + id + " not found."));
        return gsmLineMapper.toGSMLineResponseDTO(gsmLine);
    }

    public GSMLineResponseDTO getGSMLineByLineNumber(String lineNumber) {
        GSMLine gsmLine = gsmLineRepository.findByLineNumber(lineNumber)
            .orElseThrow(() -> new ResourceNotFoundException("GSM Line with number " + lineNumber + " not found."));
        return gsmLineMapper.toGSMLineResponseDTO(gsmLine);
    }

    public List<GSMLineResponseDTO> searchGSMLines(String lineNumber, LineStatus lineStatus,
                                                   String serviceFunction, String chipSerialNumber,
                                                   java.time.LocalDate chipDeliveryDateFrom, java.time.LocalDate chipDeliveryDateTo,
                                                   String pinCode, String pukCode) {
        Specification<GSMLine> spec = Specification
                .<GSMLine>where(LineSpecification.hasLineNumber(lineNumber))
                .and(LineSpecification.hasLineStatus(lineStatus))
                .and(GSMLineSpecification.hasServiceFunction(serviceFunction))
                .and(GSMLineSpecification.hasChipSerialNumber(chipSerialNumber))
                .and(GSMLineSpecification.chipDeliveryDateFrom(chipDeliveryDateFrom))
                .and(GSMLineSpecification.chipDeliveryDateTo(chipDeliveryDateTo))
                .and(GSMLineSpecification.hasPinCode(pinCode))
                .and(GSMLineSpecification.hasPukCode(pukCode));

        return gsmLineRepository.findAll(spec).stream()
                .map(gsmLineMapper::toGSMLineResponseDTO)
                .collect(Collectors.toList());
    }


}
