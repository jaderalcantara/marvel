package com.jaderalcantara.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bumptech.glide.RequestManager
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
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
class FavoritesViewModelUnitTest : KoinTest {

    lateinit var vm: FavoritesViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadCharacters_shouldCallLoadFavoritesRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadFavorites()).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = FavoritesViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(mock, times(1)).loadFavorites()
        verify(mock, times(0)).loadFavorites(anyString())
    }

    @Test
    fun loadCharacters_shouldEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadFavorites()).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = FavoritesViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(mock.loadFavorites()).willReturn(success)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = FavoritesViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.success(success.data)
        )
    }

    @Test
    fun loadCharacters_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val e = IOException("No internet connection")
        given(mock.loadFavorites()).willAnswer{throw e}
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = FavoritesViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val e = Exception("Generic erro")
        given(mock.loadFavorites()).willAnswer{throw e}
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = FavoritesViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
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
        vm = FavoritesViewModel()

        val character = mock(CharacterResponse::class.java)
        vm.removeFavorite(character)

        verify(mock, times(1)).removeFavorite(character)
    }

    @Test
    fun searchCharacter_shouldCallLoadFavoritesWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadFavorites("query")).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = FavoritesViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters("query").observeForever(observer)

        verify(mock, times(1)).loadFavorites("query")
        verify(mock, times(0)).loadFavorites()
    }
}