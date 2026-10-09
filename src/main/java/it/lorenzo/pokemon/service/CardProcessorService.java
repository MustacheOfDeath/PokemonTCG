package it.lorenzo.pokemon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzo.pokemon.model.*;
import it.lorenzo.pokemon.repository.ImportLogRepository;
import it.lorenzo.pokemon.repository.PokemonCardRepository;
import it.lorenzo.pokemon.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class CardProcessorService {

    @Autowired
    private PokemonCardRepository cardRepository;
    @Autowired
    private ImportLogRepository logRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public void processFile(Path path, PokemonSet set) {
        try {
            JsonNode root = objectMapper.readTree(path.toFile());

            if (root.isArray()) {
                for (JsonNode node : root) {
                    processSingleCard(node, path);
                }
            } else {
                processSingleCard(root, path);
            }
        } catch (Exception e) {
            logError(path, "Errore struttura file: " + e.getMessage());
        }
    }

    private void processSingleCard(JsonNode node, Path path) {
        try {
            PokemonCard cardFromJson = objectMapper.treeToValue(node, PokemonCard.class);

            if (cardFromJson.getAttacks() != null) {
                for (int i = 0; i < cardFromJson.getAttacks().size(); i++) {
                    Attack att = cardFromJson.getAttacks().get(i);
                    att.setCard(cardFromJson);
                    if (att.getId() == null) {
                        att.setId(cardFromJson.getId() + "_attack_" + i);
                    }
                }
            }

            if (cardFromJson.getWeaknesses() != null) {
                for (int i = 0; i < cardFromJson.getWeaknesses().size(); i++) {
                    Weakness w = cardFromJson.getWeaknesses().get(i);
                    w.setCard(cardFromJson);
                    if (w.getId() == null) {
                        w.setId(cardFromJson.getId() + "_weakness_" + i);
                    }
                }
            }

            cardRepository.findById(cardFromJson.getId()).ifPresentOrElse(
                    existingCard -> {
                        CommonUtils.copyNonNullProperties(cardFromJson, existingCard);
                        cardRepository.save(existingCard);
                    },
                    () -> cardRepository.save(cardFromJson)
            );
        } catch (Exception e) {
            logError(path, "Errore deserializzazione ID " + node.path("id").asText() + ": " + e.getMessage());
        }
    }

    private void logError(Path path, String message) {
        ImportLog log = new ImportLog();
        log.setFilePath(path.toString());
        log.setErrorMessage(message);
        logRepository.save(log);
        System.err.println(message);
    }


}