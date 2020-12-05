package com.jaderalcantara.marvel.feature.data

import com.jaderalcantara.marvel.feature.data.db.CharacterEntity
import com.jaderalcantara.marvel.infra.database.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.charset.StandardCharsets
import java.util.*

class LocalDataSource: KoinComponent {
    private val database: AppDatabase by inject()

    fun isFavorite(id: Int): Boolean {
        val loadById = database.userDao().loadById(id)
        return loadById != null
    }

    fun removeFavorite(character: CharacterResponse) {
        database.userDao().delete(CharacterEntity(character.id, "", ""))
    }

    fun addFavorite(character: CharacterResponse, imageBytes: ByteArray) {
        val string = String(Base64.getEncoder().encode(imageBytes), StandardCharsets.UTF_8)
        database.userDao().insert(CharacterEntity(character.id, character.name, string))
    }

    fun getCharacters(): CharactersResponse {
        val map = database.userDao().getAll().map { characterEntity ->
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

    fun searchCharacter(query: String): CharactersResponse {
        val map = database.userDao().findByName("$query%").map { characterEntity ->
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