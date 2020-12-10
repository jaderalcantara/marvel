package com.jaderalcantara.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.presentation.characterDetail.CharacterDetailViewModel
import com.jaderalcantara.marvel.infra.ImageHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class CharacterDetailViewModelUnitTest : KoinTest {

    lateinit var vm: CharacterDetailViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun removeFavorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)

        vm = CharacterDetailViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val character = mock(CharacterResponse::class.java)
        vm.removeFavorite(character)

        verify(repository, times(1)).removeFavorite(character)
    }

    @Test
    fun favorite_shouldCallAddFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val imageHelper = mock(ImageHelper::class.java)
        val bytes = "Hello".encodeToByteArray()
        given(imageHelper.downloadImage(anyString())).willReturn(bytes)

        vm = CharacterDetailViewModel(repository, coroutineRule.testDispatcher, imageHelper)

        val thumb = CharacterThumbnailResponse("","", null)
        val character = CharacterResponse(0,"", thumb, false)
        vm.favorite(character)

        verify(repository, times(1)).addFavorite(character, bytes)
    }
}