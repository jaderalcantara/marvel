package com.jaderalcantara.marvel.feature.domain

import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.CharactersResponse

class CharactersHandler(private val repository: CharacterRepository) {

    suspend fun loadCharacters(offset: Int, limit: Int, query: String? = null): CharactersResponse {
        val loadCharacters = query?.let { repository.loadCharacters(query, offset, limit) } ?: repository.loadCharacters(offset, limit)
        loadCharacters.data.results.forEach {
            it.isFavorite = repository.isFavorite(it.id)
        }
        return loadCharacters
    }
}