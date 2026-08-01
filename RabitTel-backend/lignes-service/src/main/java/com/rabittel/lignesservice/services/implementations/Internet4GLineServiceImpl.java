package com.rabittel.lignesservice.services.implementations;

import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineCreateRequestDTO;
import com.rabittel.lignesservice.dtos.request.LineRequestDTO.Internet4GLineRequestDTO.Internet4GLineUpdateRequestDTO;
import com.rabittel.lignesservice.dtos.response.Internet4GLineResponseDTO;
import com.rabittel.lignesservice.entities.Agency;
import com.rabittel.lignesservice.entities.Internet4GLine;
import com.rabittel.lignesservice.entities.Plan;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import com.rabittel.lignesservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.lignesservice.exceptions.ResourceNotFoundException;
import com.rabittel.lignesservice.mappers.Internet4GLineMapper;
import com.rabittel.lignesservice.repositories.AgencyRepository;
import com.rabittel.lignesservice.repositories.ContractRepository;
import com.rabittel.lignesservice.repositories.Internet4GLineRepository;
import com.rabittel.lignesservice.repositories.PlanRepository;
import com.rabittel.lignesservice.services.interfaces.Internet4GLineService;
import com.rabittel.lignesservice.specifications.Internet4GLineSpecification;
import com.rabittel.lignesservice.specifications.LineSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Internet4GLineServiceImpl implements Internet4GLineService {

    private final Internet4GLineRepository internet4GLineRepository;
    private final Internet4GLineMapper internet4GLineMapper;

    private final AgencyRepository agencyRepository;
    private final PlanRepository planRepository;
    private final ContractRepository contractRepository;


    public Internet4GLineResponseDTO createInternet4GLine(
            Internet4GLineCreateRequestDTO createRequestDTO) {

        if (internet4GLineRepository.existsByLineNumber(createRequestDTO.getLineNumber())) {
            throw new ResourceAlreadyExistsException(
                    "4G Line with number "
                            + createRequestDTO.getLineNumber()
                            + " already exists."
            );
        }

        if (internet4GLineRepository.existsBySimSerialNumber(createRequestDTO.getSimSerialNumber())) {
            throw new ResourceAlreadyExistsException(
                    "SIM serial number "
                            + createRequestDTO.getSimSerialNumber()
                            + " already exists."
            );
        }

        if (internet4GLineRepository.existsByEquipmentSerialNumber(createRequestDTO.getEquipmentSerialNumber())) {
            throw new ResourceAlreadyExistsException(
                    "Equipment serial number "
                            + createRequestDTO.getEquipmentSerialNumber()
                            + " already exists."
            );
        }


        Internet4GLine internet4GLine =
                internet4GLineMapper.toEntity(createRequestDTO);

        Agency agency = agencyRepository.findById(createRequestDTO.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
        Plan plan = planRepository.findById(createRequestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        internet4GLine.setAgency(agency);
        internet4GLine.setPlan(plan);

        internet4GLine.setLineType(LineType.G4);
        internet4GLine.setLineStatus(LineStatus.ACTIVE);


        Internet4GLine savedLine = internet4GLineRepository.save(internet4GLine);


        return internet4GLineMapper.toInternet4GLineResponseDTO(savedLine);
    }



    public Internet4GLineResponseDTO updateInternet4GLine(
            UUID id,
            Internet4GLineUpdateRequestDTO updateRequestDTO) {


        Internet4GLine internet4GLine =
                internet4GLineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "4G Line with id "
                                                + id
                                                + " not found."
                                ));



        if (updateRequestDTO.getLineNumber() != null
                && !updateRequestDTO.getLineNumber()
                .equals(internet4GLine.getLineNumber())
                && internet4GLineRepository.existsByLineNumber(
                updateRequestDTO.getLineNumber())) {

            throw new ResourceAlreadyExistsException(
                    "4G Line with number "
                            + updateRequestDTO.getLineNumber()
                            + " already exists."
            );
        }



        if (updateRequestDTO.getSimSerialNumber() != null
                && !updateRequestDTO.getSimSerialNumber()
                .equals(internet4GLine.getSimSerialNumber())
                && internet4GLineRepository.existsBySimSerialNumber(
                updateRequestDTO.getSimSerialNumber())) {

            throw new ResourceAlreadyExistsException(
                    "SIM serial number "
                            + updateRequestDTO.getSimSerialNumber()
                            + " already exists."
            );
        }



        if (updateRequestDTO.getEquipmentSerialNumber() != null
                && !updateRequestDTO.getEquipmentSerialNumber()
                .equals(internet4GLine.getEquipmentSerialNumber())
                && internet4GLineRepository.existsByEquipmentSerialNumber(
                updateRequestDTO.getEquipmentSerialNumber())) {

            throw new ResourceAlreadyExistsException(
                    "Equipment serial number "
                            + updateRequestDTO.getEquipmentSerialNumber()
                            + " already exists."
            );
        }



        internet4GLineMapper.updateEntityFromDto(
                updateRequestDTO,
                internet4GLine
        );



        if (updateRequestDTO.getAgencyId() != null) {

            var agency = agencyRepository.findById(
                            updateRequestDTO.getAgencyId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Agency not found."
                            ));

            internet4GLine.setAgency(agency);
        }



        if (updateRequestDTO.getPlanId() != null) {

            var plan = planRepository.findById(
                            updateRequestDTO.getPlanId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Plan not found."
                            ));

            internet4GLine.setPlan(plan);
        }



        if (updateRequestDTO.getContractId() != null) {

            var contract = contractRepository.findById(
                            updateRequestDTO.getContractId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Contract not found."
                            ));

            internet4GLine.setContract(contract);
        }






        Internet4GLine updatedLine =
                internet4GLineRepository.save(internet4GLine);


        return internet4GLineMapper
                .toInternet4GLineResponseDTO(updatedLine);
    }




    public void terminatedInternet4GLine(UUID id) {

        Internet4GLine internet4GLine =
                internet4GLineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "4G Line with id "
                                                + id
                                                + " not found."
                                ));


        if (LineStatus.ACTIVE.equals(
                internet4GLine.getLineStatus())) {

            internet4GLine.setLineStatus(
                    LineStatus.TERMINATED
            );

            internet4GLineRepository.save(internet4GLine);
        }
    }




    // Admin
    public void deleteInternet4GLine(UUID id) {

        Internet4GLine internet4GLine =
                internet4GLineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "4G Line with id "
                                                + id
                                                + " not found."
                                ));


        if (LineStatus.ACTIVE.equals(
                internet4GLine.getLineStatus())) {

            throw new IllegalStateException(
                    "Cannot delete an active 4G Line."
            );
        }


        internet4GLineRepository.delete(internet4GLine);
    }




    public List<Internet4GLineResponseDTO> getAllInternet4GLines() {

        return internet4GLineRepository.findAll()
                .stream()
                .map(internet4GLineMapper::toInternet4GLineResponseDTO)
                .collect(Collectors.toList());
    }




    public Internet4GLineResponseDTO getInternet4GLineById(UUID id) {

        Internet4GLine internet4GLine =
                internet4GLineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "4G Line not found."
                                ));

        return internet4GLineMapper
                .toInternet4GLineResponseDTO(internet4GLine);
    }




    public Internet4GLineResponseDTO getInternet4GLineByLineNumber(
            String lineNumber) {


        Internet4GLine internet4GLine =
                internet4GLineRepository.findByLineNumber(lineNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "4G Line not found."
                                ));


        return internet4GLineMapper
                .toInternet4GLineResponseDTO(internet4GLine);
    }




    public List<Internet4GLineResponseDTO> getAllInternet4GLinesByStatus(
            LineStatus lineStatus) {


        return internet4GLineRepository
                .findByLineStatus(lineStatus)
                .stream()
                .map(internet4GLineMapper::toInternet4GLineResponseDTO)
                .collect(Collectors.toList());
    }




    public List<Internet4GLineResponseDTO> getAllBillableInternet4GLines(){

        List<Internet4GLineResponseDTO> lines =
                getAllInternet4GLinesByStatus(LineStatus.ACTIVE);

        lines.addAll(
                getAllInternet4GLinesByStatus(LineStatus.SUSPENDED)
        );

        return lines;
    }





    public List<Internet4GLineResponseDTO> searchInternet4GLines(
            String lineNumber,
            LineStatus lineStatus,
            String serviceFunction,
            String simSerialNumber,
            String pinCode,
            String pukCode,
            String equipment,
            String equipmentSerialNumber,
            Long bandwidth) {


        Specification<Internet4GLine> spec =
                Specification
                        .<Internet4GLine>where(
                                LineSpecification.hasLineNumber(lineNumber)
                        )
                        .and(
                                LineSpecification.hasLineStatus(lineStatus)
                        )
                        .and(
                                Internet4GLineSpecification.hasServiceFunction(serviceFunction)
                        )
                        .and(
                                Internet4GLineSpecification.hasSimSerialNumber(simSerialNumber)
                        )
                        .and(
                                Internet4GLineSpecification.hasPinCode(pinCode)
                        )
                        .and(
                                Internet4GLineSpecification.hasPukCode(pukCode)
                        )
                        .and(
                                Internet4GLineSpecification.hasEquipment(equipment)
                        )
                        .and(
                                Internet4GLineSpecification.hasEquipmentSerialNumber(equipmentSerialNumber)
                        )
                        .and(
                                Internet4GLineSpecification.hasBandwidth(bandwidth)
                        );


        return internet4GLineRepository.findAll(spec)
                .stream()
                .map(internet4GLineMapper::toInternet4GLineResponseDTO)
                .collect(Collectors.toList());
    }

}