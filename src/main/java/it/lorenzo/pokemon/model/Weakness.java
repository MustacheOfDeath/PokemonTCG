package it.lorenzo.pokemon.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "weakness")
public class Weakness {
    @Id
    private String id;

    private String type;
    private String value;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private PokemonCard card;
}