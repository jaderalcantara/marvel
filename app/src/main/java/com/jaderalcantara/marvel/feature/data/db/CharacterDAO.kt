package com.jaderalcantara.marvel.feature.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CharacterDAO {
    @Query("SELECT * FROM CharacterEntity ORDER BY name ASC")
    suspend fun getAll(): List<CharacterEntity>

    @Query("SELECT * FROM CharacterEntity WHERE id IS :characterId")
    suspend fun loadById(characterId: Int): CharacterEntity?

    @Query("SELECT * FROM CharacterEntity WHERE name LIKE :first ORDER BY name ASC")
    suspend fun findByName(first: String): List<CharacterEntity>

    @Insert
    suspend fun insert(character: CharacterEntity)

    @Delete
    suspend fun delete(character: CharacterEntity)
}