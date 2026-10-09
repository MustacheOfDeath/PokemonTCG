package it.lorenzo.pokemon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "pokemon_cards")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonCard {

    @Id
    private String id;
    private String name;
    private String supertype;
    private String hp;
    private String number;
    private String artist;
    private String rarity;

    @Column(columnDefinition = "TEXT")
    private String flavorText;

    private String regulationMark;
    private Integer convertedRetreatCost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "set_id")
    private PokemonSet pokemonSet;

    @ElementCollection
    private List<String> subtypes;

    @ElementCollection
    private List<String> types;

    @ElementCollection
    private List<String> retreatCost;

    @ElementCollection
    private List<Integer> nationalPokedexNumbers;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Attack> attacks;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Weakness> weaknesses;

    @Embedded
    private CardImages images;

}

