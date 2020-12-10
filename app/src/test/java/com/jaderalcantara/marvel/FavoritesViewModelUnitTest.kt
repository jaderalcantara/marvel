package com.jaderalcantara.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.presentation.favorites.FavoritesViewModel
import com.jaderalcantara.marvel.infra.request.StateData
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.koin.test.KoinTest
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.io.IOException
import java.lang.Exception

@ExperimentalCoroutinesApi
class FavoritesViewModelUnitTest : KoinTest {

    lateinit var vm: FavoritesViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun loadCharacters_shouldCallLoadFavoritesRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadFavorites()).willReturn(mock(CharactersResponse::class.java))

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        verify(repository, times(1)).loadFavorites()
        verify(repository, times(0)).loadFavorites(anyString())
    }

    @Test
    fun loadCharacters_shouldEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadFavorites()).willReturn(mock(CharactersResponse::class.java))

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadFavorites()).willReturn(success)

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.success(success.data.results)
        )
    }

    @Test
    fun loadCharacters_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val e = IOException("No internet connection")
        given(repository.loadFavorites()).willAnswer{throw e}

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val e = Exception("Generic erro")
        given(repository.loadFavorites()).willAnswer{throw e}

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun removeFavorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        val character = mock(CharacterResponse::class.java)
        vm.removeFavorite(character)

        verify(repository, times(1)).removeFavorite(character)
        verify(observer).onChanged(StateData.success(ArrayList()))
    }


    @Test
    fun searchCharacter_shouldCallLoadFavoritesWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadFavorites("query")).willReturn(mock(CharactersResponse::class.java))

        vm = FavoritesViewModel(repository, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)
        vm.loadCharacters("query")

        verify(repository, times(1)).loadFavorites("query")
        verify(repository, times(0)).loadFavorites()
    }
}