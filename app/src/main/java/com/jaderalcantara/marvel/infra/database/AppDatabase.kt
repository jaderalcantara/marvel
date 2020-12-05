package com.jaderalcantara.marvel.infra.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaderalcantara.marvel.feature.data.db.CharacterDAO
import com.jaderalcantara.marvel.feature.data.db.CharacterEntity

@Database(entities = [CharacterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): CharacterDAO
}