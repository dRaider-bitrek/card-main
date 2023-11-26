package com.example.cardclient;

import com.example.cardclient.mina.MinaClient;
import com.example.cardclient.service.ApduService;
import com.example.cardclient.service.FileRqestServise;
import com.example.cardclient.service.RestartService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
public class CardClientApplication {
    private final ApduService apduService;
    private final RestartService restartService;
    private final MinaClient minaClient;
    private final FileRqestServise fileRqestServise;
    public static void main(String[] args) {
        SpringApplication.run(CardClientApplication.class, args);
    }
    @Bean
    public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
                restartService.setMinaClient(minaClient);
                restartService.restartClient();
//                    restartService.cTerminals();

//                    fileRqestServise.start("862430053478109","80e5c2c");


//                    fileRqestServise.getFile("862430053478109");
                //              nettiTCPClient.connect("localhost",8585);
//                var temp = nettiTCPClient.connect("194.28.85.206",8585);
//                apduService.setServerChannel( temp);

//                194.28.85.206

                //                tcpServer.start();
//                nettiTCPClient.connect("localhost",5001);
//                nettiTCPClient.connect("81.23.17.125",5001);

            }
        };
    }

}
