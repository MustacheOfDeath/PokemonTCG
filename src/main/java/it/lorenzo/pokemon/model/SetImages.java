package it.lorenzo.pokemon.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class SetImages {
    private String symbol;
    private String logo;

}
