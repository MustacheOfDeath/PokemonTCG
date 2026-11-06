package it.lorenzo.pokemon.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class GitService {

    public String pullLatestData() {
        StringBuilder output = new StringBuilder();
        try {
            String basePath = System.getenv("POKEMON_DATA_PATH");

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(basePath));

            processBuilder.command("git", "pull");

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Git pull executed successfully:\n" + output.toString();
            } else {
                return "Error during git pull (exit code " + exitCode + "):\n" + output.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception during git pull execution: " + e.getMessage();
        }
    }
}