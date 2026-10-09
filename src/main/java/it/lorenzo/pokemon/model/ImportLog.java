package it.lorenzo.pokemon.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ImportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime createdAt = LocalDateTime.now();
}