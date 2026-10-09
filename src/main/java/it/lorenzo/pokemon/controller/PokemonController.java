package it.lorenzo.pokemon.controller;

import it.lorenzo.pokemon.dto.PokemonCardDTO;
import it.lorenzo.pokemon.model.User;
import it.lorenzo.pokemon.repository.UserRepository;
import it.lorenzo.pokemon.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin(origins = {"http://192.168.1.169:4200", "http://localhost:4200"})
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/cards")
    public ResponseEntity<Map<String, Object>> getCards(
            @RequestParam(defaultValue = "en") String lang,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String illustratorId,
            @RequestParam(required = false) String setId,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String username) {

        User user = validateUser(username);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Map<String, Object> serviceResult = pokemonService.getCards(lang, name, illustratorId, setId, sort, page);

        Map<String, Object> response = new HashMap<>();
        response.put("cards", serviceResult.get("cards"));
        response.put("totalPages", serviceResult.get("totalPages"));
        response.put("userCollection", user.getCollection());
        response.put("userWishlist", user.getWishlist());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/collection")
    public ResponseEntity<Map<String, Object>> showCollection(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String illustratorId,
            @RequestParam(required = false) String setId,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String username) {

        User user = validateUser(username);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Map<String, Object> result = pokemonService.getFilteredCardsFromIdsPaginated(
                new ArrayList<>(user.getCollection()), "en", name, illustratorId, setId, sort, page
        );

        result.put("userCollection", user.getCollection());
        result.put("userWishlist", user.getWishlist());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/collection/full")
    public ResponseEntity<Map<String, Object>> getFullCollection(
            @RequestParam(required = false) String username) {

        User user = validateUser(username);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<PokemonCardDTO> collectionCards = pokemonService.getFilteredCardsFromIds(new ArrayList<>(user.getCollection()),"en", null, null, null);

        return ResponseEntity.ok(buildUserResponse(collectionCards, user));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<Map<String, Object>> showWishlist(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String illustratorId,
            @RequestParam(required = false) String setId,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String username) {

        User user = validateUser(username);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Map<String, Object> result = pokemonService.getFilteredCardsFromIdsPaginated(
                new ArrayList<>(user.getWishlist()), "en", name, illustratorId, setId,sort, page
        );

        result.put("userCollection", user.getCollection());
        result.put("userWishlist", user.getWishlist());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata(@RequestParam(defaultValue = "en") String lang) {
        Map<String, Object> response = new HashMap<>();
        response.put("illustrators", pokemonService.getIllustrators(lang));
        response.put("sets", pokemonService.getSets(lang));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metadata/collection")
    public ResponseEntity<Map<String, Object>> getMetadataCollection(
            @RequestParam String username,
            @RequestParam String section,
            @RequestParam(defaultValue = "en") String lang) {

        User user = validateUser(username);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<String> ids = "collection".equals(section) ?
                new ArrayList<>(user.getCollection()) :
                new ArrayList<>(user.getWishlist());

        return ResponseEntity.ok(pokemonService.getMetadataCollection(ids, lang));
    }

    @PostMapping("/{type}/toggle")
    public ResponseEntity<Map<String, Object>> toggleAction(
            @PathVariable String type,
            @RequestBody Map<String, String> payload) {

        String username = payload.get("username");
        String cardId = payload.get("cardId");

        if (username == null || cardId == null) return ResponseEntity.badRequest().build();

        if ("collection".equals(type)) pokemonService.toggleCollection(username, cardId);
        else if ("wishlist".equals(type)) pokemonService.toggleWishlist(username, cardId);
        else if ("move".equals(type)) pokemonService.moveFromWishlistToCollection(username, cardId);

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(buildUserResponse(new ArrayList<>(), user));
    }

    private User validateUser(String username) {
        if (username == null || username.trim().isEmpty() || username.equals("null")) return null;
        return userRepository.findByUsername(username).orElse(null);
    }

    private Map<String, Object> buildUserResponse(List<PokemonCardDTO> cards, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("cards", cards);
        response.put("userCollection", user.getCollection());
        response.put("userWishlist", user.getWishlist());
        return response;
    }
}