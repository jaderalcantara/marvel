package com.jaderalcantara.marvel.feature.data

import java.io.Serializable

data class CharacterThumbnailResponse (
    val path: String,
    val extension: String,
    val base64: String?
): Serializable
