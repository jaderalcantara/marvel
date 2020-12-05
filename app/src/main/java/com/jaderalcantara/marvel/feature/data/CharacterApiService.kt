package com.jaderalcantara.marvel.feature.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApiService {

    @GET("v1/public/characters")
    suspend fun getCharacters( @Query("offset") offset: Int, @Query("limit") limit: Int, @Query("orderBy") order: String): CharactersResponse

    @GET("v1/public/characters")
    suspend fun getCharacters( @Query("nameStartsWith") query: String, @Query("offset") offset: Int, @Query("limit") limit: Int, @Query("orderBy") order: String): CharactersResponse

}