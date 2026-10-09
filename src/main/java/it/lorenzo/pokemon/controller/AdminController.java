package it.lorenzo.pokemon.controller;

import it.lorenzo.pokemon.dto.AdminUserDto;
import it.lorenzo.pokemon.model.User;
import it.lorenzo.pokemon.repository.UserRepository;
import it.lorenzo.pokemon.service.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://192.168.1.169:4200", "http://localhost:4200"})
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    DataLoaderService dataLoaderService;

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        List<User> users = userRepository.findByIsAdminFalse();
        List<AdminUserDto> response = new ArrayList<>();

        for (User u : users) {
            int collectionSize = u.getCollection() != null ? u.getCollection().size() : 0;
            int wishlistSize = u.getWishlist() != null ? u.getWishlist().size() : 0;

            response.add(new AdminUserDto(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    collectionSize,
                    wishlistSize
            ));
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{username}/change-password")
    public ResponseEntity<Map<String, Object>> changePasswordByAdmin(
            @PathVariable String username, @RequestBody Map<String, String> payload) {

        String newPassword = payload.get("newPassword");
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{username}/change-email")
    public ResponseEntity<Map<String, Object>> changeEmailByAdmin(
            @PathVariable String username, @RequestBody Map<String, String> payload) {

        String newEmail = payload.get("newEmail");
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        user.setEmail(newEmail.trim().toLowerCase());
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Email changed successfully!");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{username}/collection")
    @Transactional
    public ResponseEntity<Map<String, Object>> clearCollection(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (user.getCollection() != null) {
            user.getCollection().clear();
            userRepository.save(user);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Collection cleared successfully!");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{username}/wishlist")
    @Transactional
    public ResponseEntity<Map<String, Object>> clearWishlist(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (user.getWishlist() != null) {
            user.getWishlist().clear();
            userRepository.save(user);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Wishlist cleared successfully!");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{username}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            if (user.getCollection() != null) user.getCollection().clear();
            if (user.getWishlist() != null) user.getWishlist().clear();
            userRepository.save(user);

            userRepository.delete(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account deleted successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting account!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importData() {
        String result = dataLoaderService.manualImport();
        return ResponseEntity.ok(result);
    }
}