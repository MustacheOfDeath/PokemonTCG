package it.lorenzo.pokemon.service;

import it.lorenzo.pokemon.model.AppSetting;
import it.lorenzo.pokemon.repository.AppSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduledImportService {

    @Autowired
    private GitService gitService;

    @Autowired
    private DataLoaderService dataLoaderService;

    @Autowired
    private AppSettingRepository settingRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledDailyImport() {
        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("=== Starting scheduled daily Git pull and import at " + startTime + " ===");

        try {
            String gitResult = gitService.pullLatestData();
            System.out.println(gitResult);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            saveLastImportTime(now); 

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

    private void saveLastImportTime(String timestamp) {
        AppSetting setting = settingRepository.findById("LAST_IMPORT_TIME")
                .orElse(new AppSetting());
        setting.setSettingKey("LAST_IMPORT_TIME");
        setting.setValue(timestamp);
        settingRepository.save(setting);
    }
}