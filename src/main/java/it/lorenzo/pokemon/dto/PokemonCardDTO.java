package it.lorenzo.pokemon.dto;

import it.lorenzo.pokemon.model.PokemonCard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PokemonCardDTO {
    private String id;
    private String name;
    private String supertype;
    private String hp;
    private String number;
    private String rarity;
    private String artist;
    private String flavorText;
    private List<String> types;
    private List<String> subtypes;
    private List<AttackDTO> attacks;
    private String smallImageUrl;
    private String largeImageUrl;
    private PokemonSetDTO pokemonSet;

    public PokemonCardDTO(PokemonCard card) {
        this.id = card.getId();
        this.name = card.getName();
        this.supertype = card.getSupertype();
        this.hp = card.getHp();
        this.number = card.getNumber();
        this.rarity = card.getRarity();
        this.artist = card.getArtist();
        this.flavorText = card.getFlavorText();
        this.types = card.getTypes();
        this.subtypes = card.getSubtypes();
        if (card.getImages() != null) {
            this.smallImageUrl = card.getImages().getSmall();
            this.largeImageUrl = card.getImages().getLarge();
        }
        if (card.getAttacks() != null) {
            this.attacks = card.getAttacks().stream().map(AttackDTO::new).toList();
        }

        if (card.getPokemonSet() != null) {
            this.pokemonSet = new PokemonSetDTO(card.getPokemonSet());
        }
    }
}