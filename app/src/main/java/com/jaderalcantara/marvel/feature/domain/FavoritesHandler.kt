package com.jaderalcantara.marvel.feature.domain

import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.data.api.CharacterThumbnailResponse
import com.jaderalcantara.marvel.feature.data.api.CharactersResponse
import com.jaderalcantara.marvel.feature.data.api.DataCharacterResponse
import com.jaderalcantara.marvel.feature.data.db.CharacterEntity
import com.jaderalcantara.marvel.infra.ImageHelper

class FavoritesHandler(
    private val repository: CharacterRepository,
    private val imageHelper: ImageHelper
    ) {

    suspend fun removeFavorite(character: CharacterResponse) {
        repository.removeFavorite(character)
    }

    suspend fun addFavorite(character: CharacterResponse) {
        val imageBytes: ByteArray = imageHelper.downloadImage(character.thumbnail.path + "." + character.thumbnail.extension)
        val string = imageHelper.bytesToBase64(imageBytes)
        repository.addFavorite(CharacterEntity(character.id, character.name, string))
    }

    suspend fun loadFavorites(): CharactersResponse {
        val map = repository.loadFavorites().map { characterEntity ->
            CharacterResponse(
                characterEntity.id,
                characterEntity.name,
                CharacterThumbnailResponse("", "", characterEntity.image)
            )
        }
        return CharactersResponse(
            DataCharacterResponse(0,0,0,0, map)
        )
    }

    suspend fun loadFavorites(query: String): CharactersResponse {
        val map = repository.loadFavorites(query).map { characterEntity ->
            CharacterResponse(
                characterEntity.id,
                characterEntity.name,
                CharacterThumbnailResponse("", "", characterEntity.image)
            )
        }
        return CharactersResponse(
            DataCharacterResponse(0,0,0,0, map)
        )
    }
}