package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.PokemonCard;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PokemonSpecifications {
    public static Specification<PokemonCard> filter(String name, String illustratorId, String setId, String rarity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (illustratorId != null && !illustratorId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("artist")), "%" + illustratorId.toLowerCase() + "%"));
            }
            if (setId != null && !setId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("pokemonSet").get("id"), setId));
            }
            if (rarity != null && !rarity.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("rarity"), rarity));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}