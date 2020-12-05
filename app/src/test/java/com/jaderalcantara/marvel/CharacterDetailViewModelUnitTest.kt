package com.jaderalcantara.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bumptech.glide.RequestManager
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
import com.jaderalcantara.marvel.feature.presentation.characterDetail.CharacterDetailViewModel
import com.jaderalcantara.marvel.feature.presentation.favorites.FavoritesViewModel
import com.jaderalcantara.marvel.infra.ImageHelper
import com.jaderalcantara.marvel.infra.request.StateData
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.internal.matchers.Any
import java.io.IOException
import java.lang.Exception

@ExperimentalCoroutinesApi
class CharacterDetailViewModelUnitTest : KoinTest {

    lateinit var vm: CharacterDetailViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun removeFavorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory {  mock(ImageHelper::class.java) }
        }
        startKoin { modules(appModule) }
        vm = CharacterDetailViewModel()

        val character = mock(CharacterResponse::class.java)
        vm.removeFavorite(character)

        verify(mock, times(1)).removeFavorite(character)
    }

    @Test
    fun favorite_shouldCallAddFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val imageHelper = mock(ImageHelper::class.java)
        val bytes = "Hello".encodeToByteArray()
        given(imageHelper.downloadImage(anyString())).willReturn(bytes)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { imageHelper  }
        }
        startKoin { modules(appModule) }
        vm = CharacterDetailViewModel()

        val thumb = CharacterThumbnailResponse("","", null)
        val character = CharacterResponse(0,"", thumb, false)
        vm.favorite(character)

        verify(mock, times(1)).addFavorite(character, bytes)
    }
}