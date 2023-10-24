package com.springbot.tttn.application.configs;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class LogFileConfig {

    public void cleanLogFile(String logFilePath) {
        File logFile = new File(logFilePath);
        try (FileWriter writer = new FileWriter(logFile, false)) {
            // Truncate the file by opening it in truncate mode
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
