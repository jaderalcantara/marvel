package com.jaderalcantara.marvel.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jaderalcantara.marvel.CoroutineTestRule
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.data.api.CharacterThumbnailResponse
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import com.jaderalcantara.marvel.feature.presentation.characterDetail.CharacterDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class CharacterDetailViewModelUnitTest {

    lateinit var vm: CharacterDetailViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun removeFavorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)

        vm = CharacterDetailViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val character = Mockito.mock(CharacterResponse::class.java)
        vm.removeFavorite(character)

        Mockito.verify(favoritesHandler, Mockito.times(1)).removeFavorite(character)
    }

    @Test
    fun favorite_shouldCallAddFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)

        vm = CharacterDetailViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val thumb = CharacterThumbnailResponse("", "", null)
        val character = CharacterResponse(0, "", thumb, false)
        vm.favorite(character)

        Mockito.verify(favoritesHandler, Mockito.times(1)).addFavorite(character)
    }
}