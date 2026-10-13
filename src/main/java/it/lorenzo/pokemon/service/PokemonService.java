package it.lorenzo.pokemon.service;

import it.lorenzo.pokemon.dto.*;
import it.lorenzo.pokemon.model.*;
import it.lorenzo.pokemon.repository.*;
import it.lorenzo.pokemon.utils.NaturalOrderComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PokemonService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PokemonCardRepository cardRepository;
    @Autowired
    private PokemonSetRepository setRepository;
    @Autowired
    private RarityRepository rarityRepository;

    int itemsPerPage = 25;

    public List<String> getIllustrators(String lang) {
        return cardRepository.findAllArtists();
    }

    public List<PokemonSetDTO> getSets(String lang) {
        return setRepository.findAll().stream()
                .map(PokemonSetDTO::new)
                .sorted(Comparator.comparing(PokemonSetDTO::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }


    public Map<String, Object> getCards(String lang, String name, String illustratorId, String setId, String sort, int page) {

        Specification<PokemonCard> spec = PokemonSpecifications.filter(name, illustratorId, setId);

        boolean isNumberSort = "number-asc".equals(sort) || "number-desc".equals(sort);

        List<PokemonCardDTO> resultCards;
        int totalPages = 1;

        if (isNumberSort) {
            List<PokemonCard> allCards = cardRepository.findAll(spec);

            NaturalOrderComparator comp = new NaturalOrderComparator();
            allCards.sort((a, b) -> {
                int result = comp.compare(a.getNumber(), b.getNumber());
                return "number-asc".equals(sort) ? result : -result;
            });

            int totalElements = allCards.size();
            totalPages = (int) Math.ceil((double) totalElements / itemsPerPage);
            if (totalPages == 0) totalPages = 1;

            int start = (page - 1) * itemsPerPage;
            if (start >= totalElements) {
                resultCards = new ArrayList<>();
            } else {
                int end = Math.min(start + itemsPerPage, totalElements);
                resultCards = allCards.subList(start, end).stream()
                        .map(PokemonCardDTO::new)
                        .collect(Collectors.toList());
            }
        } else {
            Page<PokemonCard> pageResult;
            Sort sorting = Sort.unsorted();

            if ("name-asc".equals(sort)) {
                sorting = Sort.by("name").ascending();
            } else if ("name-desc".equals(sort)) {
                sorting = Sort.by("name").descending();
            } else if ("release-new".equals(sort)) {
                sorting = Sort.by("pokemonSet.releaseDate").descending();
            } else if ("release-old".equals(sort)) {
                sorting = Sort.by("pokemonSet.releaseDate").ascending();
            } else if ("rarity-asc".equals(sort)) {
                sorting = Sort.by(Sort.Order.asc("r.sortOrder").nullsLast());
            } else if ("rarity-desc".equals(sort)) {
                sorting = Sort.by(Sort.Order.desc("r.sortOrder").nullsLast());
            }

            if ("rarity-asc".equals(sort) || "rarity-desc".equals(sort)) {
                pageResult = cardRepository.findAllWithRaritySorting(spec, PageRequest.of(page - 1, itemsPerPage, sorting));
            } else {
                pageResult = cardRepository.findAll(spec, PageRequest.of(page - 1, itemsPerPage, sorting));
            }

            resultCards = pageResult.getContent().stream()
                    .map(PokemonCardDTO::new)
                    .collect(Collectors.toList());

            totalPages = pageResult.getTotalPages();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cards", resultCards);
        result.put("totalPages", totalPages == 0 ? 1 : totalPages);
        return result;
    }


    public List<PokemonCardDTO> getFilteredCardsFromIds(List<String> ids, String lang, String name, String illId, String setId) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        return cardRepository.findAllById(ids).stream()
                .filter(c -> {
                    if (name != null && !name.isEmpty() && !c.getName().toLowerCase().contains(name.toLowerCase()))
                        return false;
                    if (illId != null && !illId.isEmpty() && (c.getArtist() == null || !c.getArtist().toLowerCase().contains(illId.toLowerCase())))
                        return false;
                    return setId == null || setId.isEmpty() || (c.getPokemonSet() != null && c.getPokemonSet().getId().equals(setId));
                })
                .map(PokemonCardDTO::new)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getFilteredCardsFromIdsPaginated(List<String> ids, String lang, String name, String ill, String setId, String sort, int page) {
        List<PokemonCardDTO> allCards = getFilteredCardsFromIds(ids, lang, name, ill, setId);

        if ("name-asc".equals(sort)) {
            allCards.sort(Comparator.comparing(PokemonCardDTO::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("name-desc".equals(sort)) {
            allCards.sort(Comparator.comparing(PokemonCardDTO::getName, String.CASE_INSENSITIVE_ORDER).reversed());
        } else if ("release-new".equals(sort)) {
            allCards.sort(Comparator.comparing((PokemonCardDTO c) -> c.getPokemonSet().getReleaseDate(), Comparator.nullsLast(Comparator.reverseOrder())));
        } else if ("release-old".equals(sort)) {
            allCards.sort(Comparator.comparing((PokemonCardDTO c) -> c.getPokemonSet().getReleaseDate(), Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("number-asc".equals(sort) || "number-desc".equals(sort)) {
            NaturalOrderComparator comp = new NaturalOrderComparator();
            allCards.sort((a, b) -> {
                int result = comp.compare(a.getNumber(), b.getNumber());
                return "number-asc".equals(sort) ? result : -result;
            });
        } else if ("rarity-asc".equals(sort)) {
            allCards.sort(Comparator.comparingInt(c -> getRarityRankFromDb(c.getRarity())));
        } else if ("rarity-desc".equals(sort)) {
            allCards.sort(Comparator.comparingInt((PokemonCardDTO c) -> getRarityRankFromDb(c.getRarity())).reversed());
        }

        int totalElements = allCards.size();
        int totalPages = (int) Math.ceil((double) totalElements / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        int fromIndex = (page - 1) * itemsPerPage;
        List<PokemonCardDTO> pagedCards = new ArrayList<>();
        if (fromIndex < totalElements) {
            pagedCards = allCards.subList(fromIndex, Math.min(fromIndex + itemsPerPage, totalElements));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cards", pagedCards);
        response.put("totalPages", totalPages);
        return response;
    }

    private Map<String, Integer> rarityRankCache = null;

    private int getRarityRankFromDb(String rarityName) {
        if (rarityName == null) {
            return 0;
        }

        if (rarityRankCache == null) {
            rarityRankCache = new HashMap<>();
            List<Rarity> allRarities = rarityRepository.findAll();
            for (Rarity r : allRarities) {
                rarityRankCache.put(r.getRarity(), r.getSortOrder() != null ? r.getSortOrder() : 0);
            }
        }

        return rarityRankCache.getOrDefault(rarityName, 0);
    }

    public Map<String, Object> getMetadataCollection(List<String> userCardIds, String lang) {
        List<PokemonCardDTO> userCards = getFilteredCardsFromIds(userCardIds, lang, null, null, null);

        List<String> sortedIllustrators = userCards.stream()
                .map(PokemonCardDTO::getArtist)
                .filter(a -> a != null && !a.isEmpty())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();

        List<Map<String, String>> sortedSets = userCards.stream()
                .map(PokemonCardDTO::getPokemonSet)
                .filter(s -> s != null && s.getName() != null && s.getId() != null)
                .collect(Collectors.toMap(
                        PokemonSetDTO::getId,
                        s -> s,
                        (existing, replacement) -> existing
                ))
                .values().stream()
                .sorted(Comparator.comparing(PokemonSetDTO::getName, String.CASE_INSENSITIVE_ORDER))
                .map(s -> {
                    Map<String, String> setMap = new HashMap<>();
                    setMap.put("id", s.getId());
                    setMap.put("name", s.getName());
                    return setMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("illustrators", sortedIllustrators);
        response.put("sets", sortedSets);

        return response;
    }

    public void toggleCollection(String username, String cardId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (user.getCollection().contains(cardId)) {
            user.getCollection().remove(cardId);
        } else {
            user.getCollection().add(cardId);
            user.getWishlist().remove(cardId);
        }
        userRepository.save(user);
    }

    public void toggleWishlist(String username, String cardId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (user.getWishlist().contains(cardId)) {
            user.getWishlist().remove(cardId);
        } else {
            user.getWishlist().add(cardId);
        }
        userRepository.save(user);
    }

    public void moveFromWishlistToCollection(String username, String cardId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.getWishlist().remove(cardId);
        user.getCollection().add(cardId);
        userRepository.save(user);
    }
}