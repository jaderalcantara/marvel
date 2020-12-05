package com.jaderalcantara.marvel.feature.data

import java.io.Serializable

data class CharacterResponse (
    val id: Int,
    val name: String,
    val thumbnail: CharacterThumbnailResponse,
    var isFavorite: Boolean = false,
    val description: String? = null,
    val comics: DataComicsResponse? = null
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterResponse

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
