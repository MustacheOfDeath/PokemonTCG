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

            //TODO: change this variable used for testing
            String BASE_PATH = "E:/Documenti/Programming/pokemon-tcg-data";

            File setsFile = new File(BASE_PATH + "/sets/en.json");
            List<PokemonSet> incomingSets = objectMapper.readValue(setsFile, new TypeReference<>() {});

            for (PokemonSet newSet : incomingSets) {
                setRepository.findById(newSet.getId()).ifPresentOrElse(
                        existingSet -> {
                            CommonUtils.copyNonNullProperties(newSet, existingSet);
                            setRepository.save(existingSet);
                        },
                        () -> setRepository.save(newSet)
                );
            }

            Path cardsPath = Paths.get(BASE_PATH + "/cards/en");
            try (Stream<Path> paths = Files.walk(cardsPath)) {
                List<Path> files = paths.filter(p -> p.toString().endsWith(".json")).toList();

                files.parallelStream().forEach(path -> {
                    String setId = path.getFileName().toString().replace(".json", "");
                    setRepository.findById(setId).ifPresent(set ->
                            cardProcessorService.processFile(path, set)
                    );
                });
            }

            return "Import done successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during import: " + e.getMessage();
        }
    }

}