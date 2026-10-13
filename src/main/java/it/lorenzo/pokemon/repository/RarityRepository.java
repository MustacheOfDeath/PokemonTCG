package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.Rarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RarityRepository extends JpaRepository<Rarity, Long> {

    Optional<Rarity> findByRarity(String rarity);

}