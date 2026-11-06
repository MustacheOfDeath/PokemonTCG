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

            if (gitResult.contains("Already up to date.")) {
                System.out.println("No changes detected in the Git repository. Skipping import process.");
                System.out.println("=== Scheduled daily check finished (No updates) ===");
                return;
            }

            System.out.println("Changes detected! Starting import process...");
            String importResult = dataLoaderService.manualImport();
            System.out.println(importResult);

        } catch (Exception e) {
            System.err.println("Error during scheduled import execution: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== Scheduled daily import finished ===");
    }
}