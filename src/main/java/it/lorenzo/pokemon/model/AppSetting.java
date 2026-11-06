package it.lorenzo.pokemon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "app_settings")
@Data
public class AppSetting {

    @Id
    @Column(name = "setting_key")
    private String settingKey;

    private String value;
}