package com.jaderalcantara.marvel.feature.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CharacterThumbnailResponse (
    val path: String,
    val extension: String,
    val base64: String?
): Parcelable
