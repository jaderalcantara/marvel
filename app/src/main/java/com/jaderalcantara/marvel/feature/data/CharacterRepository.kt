package com.jaderalcantara.marvel.feature.data

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CharacterRepository: KoinComponent {

    private val api : CharacterApiService by inject()
    private val local : LocalDataSource by inject()

    suspend fun loadCharacters(offset: Int, limit: Int): CharactersResponse {

        return api.getCharacters(offset, limit, "name")
    }

    suspend fun loadCharacters(query: String, offset: Int, limit: Int): CharactersResponse {
        return api.getCharacters(query, offset, limit, "name")
    }

    fun removeFavorite(character: CharacterResponse) {
        local.removeFavorite(character)
    }

    fun addFavorite(character: CharacterResponse, imageBytes: ByteArray) {
        local.addFavorite(character, imageBytes)
    }

    fun isFavorite(id: Int): Boolean {
        return local.isFavorite(id)
    }

    fun loadFavorites(): CharactersResponse {
        return local.getCharacters()
    }

    fun loadFavorites(query: String): CharactersResponse {
        return local.searchCharacter(query)
    }
}