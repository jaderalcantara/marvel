package com.jaderalcantara.marvel.feature.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CharacterResponse (
    val id: Int,
    val name: String,
    val thumbnail: CharacterThumbnailResponse,
    var isFavorite: Boolean = false,
    val description: String? = null,
    val comics: DataComicsResponse? = null
): Parcelable {

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
