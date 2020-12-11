package com.jaderalcantara.marvel.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jaderalcantara.marvel.CoroutineTestRule
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.data.api.CharacterThumbnailResponse
import com.jaderalcantara.marvel.feature.data.db.CharacterEntity
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import com.jaderalcantara.marvel.infra.ImageHelper
import com.nhaarman.mockitokotlin2.given
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class FavoritesHandlerUnitTest {

    lateinit var favoritesHandler: FavoritesHandler

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun removeFavorite_shouldCallRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val imageHelper = Mockito.mock(ImageHelper::class.java)
        val character = CharacterResponse(0,"", CharacterThumbnailResponse("","", null))

        favoritesHandler = FavoritesHandler(characterRepository, imageHelper)

        favoritesHandler.removeFavorite(character)

        Mockito.verify(characterRepository, Mockito.times(1)).removeFavorite(character)
    }

    @Test
    fun addFavorite_shouldCallRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val imageHelper = Mockito.mock(ImageHelper::class.java)
        val character = CharacterResponse(0,"", CharacterThumbnailResponse("","", null))
        val bytes = "Hello".encodeToByteArray()
        given(imageHelper.downloadImage(".")).willReturn(bytes)
        given(imageHelper.bytesToBase64(bytes)).willReturn("")

        favoritesHandler = FavoritesHandler(characterRepository, imageHelper)

        favoritesHandler.addFavorite(character)
        val characterEntity = CharacterEntity(character.id, character.name, "")
        Mockito.verify(characterRepository, Mockito.times(1)).addFavorite(characterEntity)
    }

    @Test
    fun loadFavorites_shouldMapToCharacter() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val characterEntenty = CharacterEntity(1,"Jane", "image")
        val imageHelper = Mockito.mock(ImageHelper::class.java)

        given(characterRepository.loadFavorites()).willReturn(listOf(characterEntenty))
        favoritesHandler = FavoritesHandler(characterRepository, imageHelper)

        val loadFavorites = favoritesHandler.loadFavorites()

        assert(loadFavorites.data.results[0].id.equals(characterEntenty.id))
        assert(loadFavorites.data.results[0].name.equals(characterEntenty.name))
        assert(loadFavorites.data.results[0].thumbnail.base64.equals("image"))
    }

    @Test
    fun loadFavorites_WithQuery_shouldMapToCharacter() = coroutineRule.testDispatcher.runBlockingTest {
        val characterRepository = Mockito.mock(CharacterRepository::class.java)
        val characterEntenty = CharacterEntity(1,"Jane", "image")
        val imageHelper = Mockito.mock(ImageHelper::class.java)

        given(characterRepository.loadFavorites("query")).willReturn(listOf(characterEntenty))
        favoritesHandler = FavoritesHandler(characterRepository, imageHelper)

        val loadFavorites = favoritesHandler.loadFavorites("query")

        assert(loadFavorites.data.results[0].id.equals(characterEntenty.id))
        assert(loadFavorites.data.results[0].name.equals(characterEntenty.name))
        assert(loadFavorites.data.results[0].thumbnail.base64.equals("image"))
    }

}