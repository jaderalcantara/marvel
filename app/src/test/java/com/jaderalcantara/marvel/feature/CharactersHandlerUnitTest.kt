package com.jaderalcantara.marvel.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jaderalcantara.marvel.CoroutineTestRule
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.data.api.CharacterThumbnailResponse
import com.jaderalcantara.marvel.feature.data.api.CharactersResponse
import com.jaderalcantara.marvel.feature.data.api.DataCharacterResponse
import com.jaderalcantara.marvel.feature.domain.CharactersHandler
import com.nhaarman.mockitokotlin2.given
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class CharactersHandlerUnitTest {

    lateinit var charactersHandler: CharactersHandler

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun loadCharacters_WithoutQuery_shouldCallRepositoryWithoutQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(characterRepository.loadCharacters(0,20)).willReturn(success)
        charactersHandler = CharactersHandler(characterRepository)

        charactersHandler.loadCharacters(0,20)

        Mockito.verify(characterRepository, Mockito.times(1)).loadCharacters(0,20)
    }

    @Test
    fun loadCharacters_WithQuery_shouldCallRepositoryWithQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(characterRepository.loadCharacters("query",0,20 )).willReturn(success)
        charactersHandler = CharactersHandler(characterRepository)

        charactersHandler.loadCharacters(0,20, "query")

        Mockito.verify(characterRepository, Mockito.times(1)).loadCharacters("query", 0,20, )
    }

    @Test
    fun loadCharacters_shouldFillFavoriteField() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        val character = CharacterResponse(0,"", CharacterThumbnailResponse("","", null))
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, listOf(character)))
        given(characterRepository.loadCharacters(0,20 )).willReturn(success)
        given(characterRepository.isFavorite(0)).willReturn(true)
        charactersHandler = CharactersHandler(characterRepository)

        charactersHandler.loadCharacters(0,20)

        assert(character.isFavorite)
    }

}