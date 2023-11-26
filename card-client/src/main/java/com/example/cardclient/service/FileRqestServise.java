package com.example.cardclient.service;

import com.example.cardclient.entity.FileBlob;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FileRqestServise {
    private RestTemplate restTemplate = new RestTemplate();
    private final String BIN = "http://194.28.85.206:8181";
    final private String HOST = "http://justrack.com.ua:8190";
    final private String LOCAL = "http://localhost:8190";



//    @SneakyThrows
    @Async
    public void getFile(String imei){
        log.info("[Start RESPONSE]: ");
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(BIN).path("/file/fileblob")
                .queryParam("imei", imei)
                .build();
        try {
            var response= restTemplate.getForEntity(builder.toUri(), FileBlob.class);
            log.info("[RESPONSE]: {}",response.getBody().getStatus());
            if (response.getBody().getStatus().equals("OK")){
                Files.write(response.getBody().getFile(),new File("temp.DDD"));
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void start(String imei,String token){
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(HOST).path("/api/exat")
                .queryParam("imei", imei)
                .queryParam("token", token)
                .build();
        HttpEntity<String> request = new HttpEntity("SENS:TAHO\r\n");
        var respone = restTemplate.postForObject(builder.toUriString(),request,String.class);

    }

}
