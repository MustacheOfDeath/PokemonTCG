package it.lorenzo.pokemon.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class CardImages {
    private String small;
    private String large;
}
