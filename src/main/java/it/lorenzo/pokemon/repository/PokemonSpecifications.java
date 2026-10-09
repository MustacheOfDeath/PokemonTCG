package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.PokemonCard;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PokemonSpecifications {
    public static Specification<PokemonCard> filter(String name, String illustratorId, String setId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Long.class != query.getResultType()) {
                root.fetch("pokemonSet", JoinType.LEFT);
            }
            query.distinct(true);

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (illustratorId != null && !illustratorId.isEmpty()) {
                predicates.add(cb.equal(root.get("artist"), illustratorId));
            }
            if ( setId!= null && !setId.isEmpty()) {
                predicates.add(cb.equal(root.get("pokemonSet").get("id"), setId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}