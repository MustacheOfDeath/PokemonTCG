package it.lorenzo.pokemon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ScheduledImportService {

    @Autowired
    private GitService gitService;

    @Autowired
    private DataLoaderService dataLoaderService;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledDailyImport() {
        System.out.println("=== Starting scheduled daily Git pull and import at " + LocalDateTime.now() + " ===");

        try {
            String gitResult = gitService.pullLatestData();
            System.out.println(gitResult);

            String importResult = dataLoaderService.manualImport();
            System.out.println(importResult);

        } catch (Exception e) {
            System.err.println("Error during scheduled import execution: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== Scheduled daily import finished ===");
    }
}