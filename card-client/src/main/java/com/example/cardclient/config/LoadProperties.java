package com.example.cardclient.config;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.Properties;

@Component
@Data
@Slf4j
public class LoadProperties {
    private String uuid;
    private String url;

    @SneakyThrows
    @PostConstruct
    private void init(){
//        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        log.info("rootPath = {}",rootPath);

       String dir = System.getProperty("user.dir");
        log.info("dir = {}",dir);

        String appConfigPath = dir + "\\key.properties";
        log.info("appConfigPath = {}",appConfigPath);

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        uuid = appProps.getProperty("uuid");
        log.info("UUID = {}",uuid);
        url = appProps.getProperty("url");
        log.info("URL = {}",url);

    }

}
