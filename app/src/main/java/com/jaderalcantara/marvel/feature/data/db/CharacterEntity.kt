package com.jaderalcantara.marvel.feature.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CharacterEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image") val image: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}