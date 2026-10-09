package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.PokemonSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonSetRepository extends JpaRepository<PokemonSet, String> {
}