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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            List<JsonNode> nodes = new ArrayList<>();

            if (root.isArray()) {
                root.forEach(nodes::add);
            } else {
                nodes.add(root);
            }

            if (nodes.isEmpty()) return;

            List<String> cardIds = nodes.stream()
                    .map(n -> n.path("id").asText())
                    .filter(id -> id != null && !id.isEmpty())
                    .toList();

            Map<String, PokemonCard> existingCardsMap = cardRepository.findAllById(cardIds)
                    .stream()
                    .collect(Collectors.toMap(PokemonCard::getId, Function.identity()));

            List<PokemonCard> cardsToSave = new ArrayList<>();

            for (JsonNode node : nodes) {
                try {
                    PokemonCard cardFromJson = objectMapper.treeToValue(node, PokemonCard.class);
                    if (cardFromJson.getId() == null) continue;

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

                    PokemonCard existingCard = existingCardsMap.get(cardFromJson.getId());

                    if (existingCard == null) {
                        cardsToSave.add(cardFromJson);
                    } else {
                        if (hasCardChanges(cardFromJson, existingCard)) {
                            CommonUtils.copyNonNullProperties(cardFromJson, existingCard);
                            cardsToSave.add(existingCard);
                        }
                    }
                } catch (Exception e) {
                    logError(path, "Deserialization error for ID " + node.path("id").asText() + ": " + e.getMessage());
                }
            }

            if (!cardsToSave.isEmpty()) {
                cardRepository.saveAll(cardsToSave);
            }

        } catch (Exception e) {
            logError(path, "File structure error: " + e.getMessage());
        }
    }

    private boolean hasCardChanges(PokemonCard incoming, PokemonCard existing) {
        if (incoming.getName() != null && !Objects.equals(incoming.getName(), existing.getName())) return true;
        if (incoming.getSupertype() != null && !Objects.equals(incoming.getSupertype(), existing.getSupertype())) return true;
        if (incoming.getHp() != null && !Objects.equals(incoming.getHp(), existing.getHp())) return true;
        if (incoming.getNumber() != null && !Objects.equals(incoming.getNumber(), existing.getNumber())) return true;
        if (incoming.getArtist() != null && !Objects.equals(incoming.getArtist(), existing.getArtist())) return true;
        if (incoming.getRarity() != null && !Objects.equals(incoming.getRarity(), existing.getRarity())) return true;
        if (incoming.getFlavorText() != null && !Objects.equals(incoming.getFlavorText(), existing.getFlavorText())) return true;
        if (incoming.getRegulationMark() != null && !Objects.equals(incoming.getRegulationMark(), existing.getRegulationMark())) return true;
        if (incoming.getConvertedRetreatCost() != null && !Objects.equals(incoming.getConvertedRetreatCost(), existing.getConvertedRetreatCost())) return true;

        if (incoming.getSubtypes() != null && !Objects.equals(incoming.getSubtypes(), existing.getSubtypes())) return true;
        if (incoming.getTypes() != null && !Objects.equals(incoming.getTypes(), existing.getTypes())) return true;
        if (incoming.getRetreatCost() != null && !Objects.equals(incoming.getRetreatCost(), existing.getRetreatCost())) return true;
        if (incoming.getNationalPokedexNumbers() != null && !Objects.equals(incoming.getNationalPokedexNumbers(), existing.getNationalPokedexNumbers())) return true;

        if (incoming.getImages() != null && !Objects.equals(incoming.getImages(), existing.getImages())) return true;

        if (incoming.getAttacks() != null && !Objects.equals(incoming.getAttacks(), existing.getAttacks())) return true;
        if (incoming.getWeaknesses() != null && !Objects.equals(incoming.getWeaknesses(), existing.getWeaknesses())) return true;

        return false;
    }

    private void logError(Path path, String message) {
        ImportLog log = new ImportLog();
        log.setFilePath(path.toString());
        log.setErrorMessage(message);
        logRepository.save(log);
        System.err.println(message);
    }
}