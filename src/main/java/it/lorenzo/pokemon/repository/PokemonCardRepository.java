package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, String>, JpaSpecificationExecutor<PokemonCard> {
    
    @Query("SELECT DISTINCT c.artist FROM PokemonCard c WHERE c.artist IS NOT NULL ORDER BY c.artist ASC")
    List<String> findAllArtists();
}