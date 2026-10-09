package it.lorenzo.pokemon.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "attack")
@Data
public class Attack {
    @Id
    private String id;

    private String name;
    private Integer convertedEnergyCost;
    private String damage;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ElementCollection
    private List<String> cost;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private PokemonCard card;
}
