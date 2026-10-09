package it.lorenzo.pokemon.controller;

import it.lorenzo.pokemon.model.User;
import it.lorenzo.pokemon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{username}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable String username, @RequestBody Map<String, String> payload) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAvatarUrl(payload.get("avatar"));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Avatar updated"));
    }

    @GetMapping("/{username}/avatar")
    public ResponseEntity<Map<String, String>> getAvatar(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(Map.of("avatar", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""));
    }
}