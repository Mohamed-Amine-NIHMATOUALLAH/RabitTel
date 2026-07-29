package com.rabittel.lignesservice.schedulers;

import com.rabittel.lignesservice.services.implementations.ContractServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractScheduler {


    private final ContractServiceImpl expirationService;


    @Scheduled(cron="0 0 1 * * *")
    public void checkExpiredContracts(){

        expirationService.updateExpiredContracts();

    }
}