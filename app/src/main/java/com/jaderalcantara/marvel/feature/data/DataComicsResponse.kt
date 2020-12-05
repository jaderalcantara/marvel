package com.jaderalcantara.marvel.feature.data

import java.io.Serializable


data class DataComicsResponse (
    val items: List<ComicResponse>?
) : Serializable