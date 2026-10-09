package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.ImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportLogRepository extends JpaRepository<ImportLog, Long> {
    void deleteAll();
}