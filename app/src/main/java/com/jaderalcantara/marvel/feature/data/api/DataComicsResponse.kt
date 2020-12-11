package com.jaderalcantara.marvel.feature.data.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataComicsResponse (
    val items: List<ComicResponse>?
): Parcelable