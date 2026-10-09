package it.lorenzo.pokemon.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_collection", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "card_id")
    private Set<String> collection = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_wishlist", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "card_id")
    private Set<String> wishlist = new HashSet<>();

    @Column(name = "reset_otp", length = 10)
    private String resetOtp;

    @Column(name = "otp_expires_at")
    private LocalDateTime otpExpiresAt;

    @Column(length = 1000)
    private String pendingPassword;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
}