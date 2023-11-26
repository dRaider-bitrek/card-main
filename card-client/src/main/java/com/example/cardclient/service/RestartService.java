package com.example.cardclient.service;

import com.example.cardclient.mina.MinaClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Data
public class RestartService {

    private MinaClient minaClient;
    private final ApduService apduService;
    private String ID;

    @SneakyThrows
    @Async
    public void restartClient(){
        minaClient.tearDown();
        ID = apduService.getSerialNumber();
        log.info("Serial: {}",ID);

        while (ID==null || !minaClient.init(ID)){
            log.info("Restart client ...");
            Thread.sleep(30000);
            ID = apduService.getSerialNumber();
            log.info("Serial: {}",ID);
        }
        log.info("Client Started");

    }

//    @SneakyThrows
    @SneakyThrows
    @Async
    public void cTerminals(){
        while (true) {
            try {
                apduService.getTerminal();
                log.info("Terminal");

            } catch (Exception exception) {
                log.info(exception.getMessage());
            }
            Thread.sleep(10000);
        }
    }

}
