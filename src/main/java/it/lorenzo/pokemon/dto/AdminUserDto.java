package it.lorenzo.pokemon.dto;

public record AdminUserDto(Long id, String username, String email, int collectionSize, int wishlistSize) {}