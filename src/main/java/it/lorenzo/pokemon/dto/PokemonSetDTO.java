package it.lorenzo.pokemon.dto;

import it.lorenzo.pokemon.model.PokemonSet;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PokemonSetDTO {
    private String id;
    private String name;
    private String series;
    private int printedTotal;
    private int total;
    private String ptcgoCode;
    private LocalDate releaseDate;
    private String symbolUrl;
    private String logoUrl;

    public PokemonSetDTO(PokemonSet set) {
        this.id = set.getId();
        this.name = set.getName();
        this.series = set.getSeries();
        this.printedTotal = set.getPrintedTotal();
        this.total = set.getTotal();
        this.ptcgoCode = set.getPtcgoCode();
        this.releaseDate = set.getReleaseDate();

        if (set.getImages() != null) {
            this.symbolUrl = set.getImages().getSymbol();
            this.logoUrl = set.getImages().getLogo();
        }
    }
}