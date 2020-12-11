package com.jaderalcantara.marvel.feature.data

import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.data.db.CharacterEntity
import com.jaderalcantara.marvel.infra.database.AppDatabase

class LocalDataSource(private val database: AppDatabase) {

    suspend fun isFavorite(id: Int): Boolean {
        val loadById = database.userDao().loadById(id)
        return loadById != null
    }

    suspend fun removeFavorite(character: CharacterResponse) {
        database.userDao().delete(CharacterEntity(character.id, "", ""))
    }

    suspend fun addFavorite(character: CharacterEntity) {
        database.userDao().insert(character)
    }

    suspend fun getCharacters(): List<CharacterEntity> {
        return database.userDao().getAll()
    }

    suspend fun searchCharacter(query: String): List<CharacterEntity> {
        return database.userDao().findByName("$query%")
    }

}