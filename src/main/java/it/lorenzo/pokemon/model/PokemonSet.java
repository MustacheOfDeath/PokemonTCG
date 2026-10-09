package it.lorenzo.pokemon.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pokemon_sets")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonSet {
    @Id
    private String id;
    private String name;
    private String series;
    private int printedTotal;
    private int total;
    private String ptcgoCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate releaseDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Embedded
    private SetImages images;

    @JsonIgnore
    @OneToMany(mappedBy = "pokemonSet", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PokemonCard> cards;
}

