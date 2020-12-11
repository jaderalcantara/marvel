package com.jaderalcantara.marvel.feature.data.api

data class DataCharacterResponse (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<CharacterResponse>
)