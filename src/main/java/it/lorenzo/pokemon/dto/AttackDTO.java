package it.lorenzo.pokemon.dto;

import it.lorenzo.pokemon.model.Attack;
import lombok.Data;

import java.util.List;

@Data
public class AttackDTO {
    private final String name;
    private final String damage;
    private final String text;
    private final List<String> cost;

    public AttackDTO(Attack attack) {
        this.name = attack.getName();
        this.damage = attack.getDamage();
        this.text = attack.getText();
        this.cost = attack.getCost();
    }
}