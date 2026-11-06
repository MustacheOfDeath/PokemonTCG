package it.lorenzo.pokemon.repository;

import it.lorenzo.pokemon.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
}