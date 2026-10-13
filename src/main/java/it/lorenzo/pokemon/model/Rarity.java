package it.lorenzo.pokemon.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rarities")
@Data
public class Rarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rarity;

    @Column(name = "sort_order")
    private Integer sortOrder;

}