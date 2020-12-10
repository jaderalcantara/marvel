package com.jaderalcantara.marvel.feature.data

import com.jaderalcantara.marvel.feature.data.db.CharacterEntity

class CharacterRepository(
    private val api : CharacterRemoteDataSource,
    private val local : LocalDataSource
) {

    suspend fun loadCharacters(offset: Int, limit: Int): CharactersResponse {
        return api.getCharacters(offset, limit, "name")
    }

    suspend fun loadCharacters(query: String, offset: Int, limit: Int): CharactersResponse {
        return api.getCharacters(query, offset, limit, "name")
    }

    suspend fun removeFavorite(character: CharacterResponse) {
        local.removeFavorite(character)
    }

    suspend fun addFavorite(character: CharacterEntity) {
        local.addFavorite(character)
    }

    suspend fun isFavorite(id: Int): Boolean {
        return local.isFavorite(id)
    }

    suspend fun loadFavorites(): List<CharacterEntity> {
        return local.getCharacters()
    }

    suspend fun loadFavorites(query: String): List<CharacterEntity> {
        return local.searchCharacter(query)
    }
}