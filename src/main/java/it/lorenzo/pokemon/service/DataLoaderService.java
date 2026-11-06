package it.lorenzo.pokemon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzo.pokemon.model.PokemonSet;
import it.lorenzo.pokemon.repository.PokemonSetRepository;
import it.lorenzo.pokemon.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DataLoaderService {

    @Autowired
    private PokemonSetRepository setRepository;

    @Autowired
    private CardProcessorService cardProcessorService;

    @Autowired
    private ObjectMapper objectMapper;

    public String manualImport() {
        try {
            String basePath = System.getenv("POKEMON_DATA_PATH");

            File setsFile = new File(basePath + "/sets/en.json");
            List<PokemonSet> incomingSets = objectMapper.readValue(setsFile, new TypeReference<>() {
            });

            int updatedCount = 0;
            int insertedCount = 0;
            int skippedCount = 0;

            for (PokemonSet newSet : incomingSets) {
                Optional<PokemonSet> existingOpt = setRepository.findById(newSet.getId());

                if (existingOpt.isPresent()) {
                    PokemonSet existingSet = existingOpt.get();

                    boolean isModified = hasSignificantChanges(newSet, existingSet);

                    if (isModified) {
                        CommonUtils.copyNonNullProperties(newSet, existingSet);
                        setRepository.save(existingSet);
                        updatedCount++;
                    } else {
                        skippedCount++;
                    }
                } else {
                    setRepository.save(newSet);
                    insertedCount++;
                }
            }

            Path cardsPath = Paths.get(basePath + "/cards/en");
            try (Stream<Path> paths = Files.walk(cardsPath)) {
                List<Path> files = paths.filter(p -> p.toString().endsWith(".json")).toList();

                files.parallelStream().forEach(path -> {
                    String setId = path.getFileName().toString().replace(".json", "");
                    setRepository.findById(setId).ifPresent(set ->
                            cardProcessorService.processFile(path, set)
                    );
                });
            }

            return String.format("Import completed! Inserted: %d, Updated: %d, Skipped (unchanged): %d",
                    insertedCount, updatedCount, skippedCount);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during import: " + e.getMessage();
        }
    }

    private boolean hasSignificantChanges(PokemonSet incoming, PokemonSet existing) {
        if (incoming.getName() != null && !incoming.getName().equals(existing.getName())) {
            return true;
        }
        if (incoming.getSeries() != null && !incoming.getSeries().equals(existing.getSeries())) {
            return true;
        }
        if (incoming.getPrintedTotal() > 0 && incoming.getPrintedTotal() != existing.getPrintedTotal()) {
            return true;
        }
        if (incoming.getTotal() > 0 && incoming.getTotal() != existing.getTotal()) {
            return true;
        }

        return false;
    }
}