package com.springbot.tttn;

import com.springbot.tttn.application.configs.LogFileConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TttnApplication {

    @Autowired
    private LogFileConfig logFileConfig;

    public static void main(String[] args) {
        SpringApplication.run(TttnApplication.class, args);
    }

    @PostConstruct
    public void cleanLogFileOnStartup() {
        // Provide the path to your log file
        String logFilePath = "combine.log";
        logFileConfig.cleanLogFile(logFilePath);
    }
}
