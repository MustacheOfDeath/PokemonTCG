package it.lorenzo.pokemon.controller;

import it.lorenzo.pokemon.model.User;
import it.lorenzo.pokemon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://192.168.1.169:4200", "http://localhost:4200"})
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JavaMailSender mailSender;

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.get("username"), loginRequest.get("password")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUsername(loginRequest.get("username")).orElse(null);
            Map<String, Object> response = createResponse(true, "Login successful");
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            response.put("username", user.getUsername());
            response.put("isAdmin", user.isAdmin());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse(false, "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.get("username")).isPresent())
            return ResponseEntity.badRequest().body(createResponse(false, "Username already taken!"));

        User newUser = new User();
        newUser.setUsername(signUpRequest.get("username"));
        newUser.setPassword(passwordEncoder.encode(signUpRequest.get("password")));
        newUser.setEmail(signUpRequest.get("email").toLowerCase());
        userRepository.save(newUser);
        return ResponseEntity.ok(createResponse(true, "Register successful"));
    }

    @PostMapping("/password-reset/request")
    @Transactional
    public ResponseEntity<Map<String, Object>> requestPasswordChange(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponse(false, "Trainer not found!"));

        String otpCode = String.valueOf((int) ((Math.random() * 900000) + 100000));
        user.setResetOtp(otpCode);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setPendingPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(" PokéCollector - OTP");
        message.setText("Hi " + username + ", your OTP is: " + otpCode);
        mailSender.send(message);
        return ResponseEntity.ok(createResponse(true, "OTP has been sent!"));
    }

    @PostMapping("/password-reset/confirm")
    @Transactional
    public ResponseEntity<Map<String, Object>> confirmPasswordChange(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otpCode = request.get("otpCode");

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !otpCode.equals(user.getResetOtp()))
            return ResponseEntity.badRequest().body(createResponse(false, "OTP does not match!"));

        user.setPassword(user.getPendingPassword());
        user.setResetOtp(null);
        user.setPendingPassword(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
        return ResponseEntity.ok(createResponse(true, "Password updated successfully!"));
    }

    @PostMapping("/forgot-password/request")
    @Transactional
    public ResponseEntity<Map<String, Object>> forgotPasswordRequest(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(createResponse(true, "OTP sent successfully!"));

        String otpCode = String.valueOf((int) ((Math.random() * 900000) + 100000));
        user.setResetOtp(otpCode);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password reset");
        message.setText("Your OTP is:  " + otpCode);
        mailSender.send(message);
        return ResponseEntity.ok(createResponse(true, "OTP has been sent!"));
    }

    @PostMapping("/forgot-password/confirm")
    @Transactional
    public ResponseEntity<Map<String, Object>> forgotPasswordConfirm(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !otp.equals(user.getResetOtp()))
            return ResponseEntity.badRequest().body(createResponse(false, "OTP does not match!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
        return ResponseEntity.ok(createResponse(true, "Password updated successfully!"));
    }

    @DeleteMapping("/account/{username}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable String username) {
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
        return ResponseEntity.ok(createResponse(true, "Account deleted successfully!"));
    }
}