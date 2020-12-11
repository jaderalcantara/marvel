package com.jaderalcantara.marvel.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jaderalcantara.marvel.CoroutineTestRule
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.data.api.CharactersResponse
import com.jaderalcantara.marvel.feature.data.api.DataCharacterResponse
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import com.jaderalcantara.marvel.feature.presentation.favorites.FavoritesViewModel
import com.jaderalcantara.marvel.infra.request.StateData
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.io.IOException

@ExperimentalCoroutinesApi
class FavoritesViewModelUnitTest {

    lateinit var vm: FavoritesViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun loadCharacters_shouldCallLoadFavoritesRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        BDDMockito.given(favoritesHandler.loadFavorites())
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        Mockito.verify(favoritesHandler, Mockito.times(1)).loadFavorites()
        Mockito.verify(favoritesHandler, Mockito.times(0)).loadFavorites(anyString())
    }

    @Test
    fun loadCharacters_shouldEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        BDDMockito.given(favoritesHandler.loadFavorites())
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        BDDMockito.given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        BDDMockito.given(favoritesHandler.loadFavorites()).willReturn(success)

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.success(success.data.results)
        )
    }

    @Test
    fun loadCharacters_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val e = IOException("No internet connection")
        BDDMockito.given(favoritesHandler.loadFavorites()).willAnswer{throw e}

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val e = IOException("Generic error")
        BDDMockito.given(favoritesHandler.loadFavorites()).willAnswer{throw e}

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun removeFavorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        val character = Mockito.mock(CharacterResponse::class.java)
        vm.removeFavorite(character)

        Mockito.verify(favoritesHandler, Mockito.times(1)).removeFavorite(character)
        Mockito.verify(observer).onChanged(StateData.success(ArrayList()))
    }


    @Test
    fun searchCharacter_shouldCallLoadFavoritesWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        BDDMockito.given(favoritesHandler.loadFavorites("query"))
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = FavoritesViewModel(favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters("query")

        Mockito.verify(favoritesHandler, Mockito.times(1)).loadFavorites("query")
        Mockito.verify(favoritesHandler, Mockito.times(0)).loadFavorites()
    }
}