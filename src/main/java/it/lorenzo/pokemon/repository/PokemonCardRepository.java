package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.PokemonCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, String>, JpaSpecificationExecutor<PokemonCard> {
    
    @Query("SELECT DISTINCT c.artist FROM PokemonCard c WHERE c.artist IS NOT NULL ORDER BY c.artist ASC")
    List<String> findAllArtists();

    @Query(value = "SELECT c FROM PokemonCard c LEFT JOIN Rarity r ON c.rarity = r.rarity",
            countQuery = "SELECT count(c) FROM PokemonCard c")
    Page<PokemonCard> findAllWithRaritySorting(Specification<PokemonCard> spec, Pageable pageable);
}