package com.jaderalcantara.marvel.feature.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ComicResponse (
    val name: String
): Parcelable
