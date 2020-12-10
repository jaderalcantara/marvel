package com.jaderalcantara.marvel.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jaderalcantara.marvel.CoroutineTestRule
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.CharacterThumbnailResponse
import com.jaderalcantara.marvel.feature.data.CharactersResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.feature.domain.CharactersHandler
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
import com.jaderalcantara.marvel.infra.request.StateData
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.isNull
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import java.io.IOException
import java.lang.Exception

@ExperimentalCoroutinesApi
class AllViewModelUnitTest {

    lateinit var vm: AllViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun loadCharacters_shouldCallLoadCharactersRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        given(charactersHandler.loadCharacters(0, 20))
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        vm.loadCharacters()

        Mockito.verify(charactersHandler, Mockito.times(1)).loadCharacters(0, 20)
    }

    @Test
    fun loadCharacters_OffsetZero_shouldEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        given(charactersHandler.loadCharacters(0, 20))
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_OffsetNotZero_shouldNotEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20)).willReturn(success)
        given(charactersHandler.loadCharacters(20, 20)).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()
        vm.loadCharacters()

        Mockito.verify(observer, Mockito.times(1)).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20)).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.success(success.data.results)
        )
    }

    @Test
    fun loadCharacters_SuccessReturnNoMoreItems_shouldDisableEndlessScroll() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(20, 0, 10, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20)).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<Boolean>>()
        vm.disableEndlessLiveData.observeForever(observer)

        vm.loadCharacters()

        Mockito.verify(observer).onChanged(true)
    }

    @Test
    fun loadCharacters_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val e = IOException("No internet connection")
        given(charactersHandler.loadCharacters(0, 20)).willAnswer{throw e}

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val e = Exception("Generic erro")
        given(charactersHandler.loadCharacters(0, 20)).willAnswer{throw e}

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        Mockito.verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun reloadCharacters_shouldCallWithZeroOffset() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20)).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()
        vm.reloadCharacters()

        Mockito.verify(charactersHandler, Mockito.times(2)).loadCharacters(0, 20)
    }

    @Test
    fun favorite_shouldCallAddFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val thumb = CharacterThumbnailResponse("", "", null)
        val character = CharacterResponse(0, "", thumb, false)
        vm.favorite(character, false)

        Mockito.verify(favoritesHandler, Mockito.times(1)).addFavorite(character)
    }

    @Test
    fun favorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val character = Mockito.mock(CharacterResponse::class.java)
        vm.favorite(character, true)

        Mockito.verify(favoritesHandler, Mockito.times(1)).removeFavorite(character)
    }

    @Test
    fun searchCharacter_shouldCallLoadCharactersWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        given(charactersHandler.loadCharacters(0, 20))
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        Mockito.verify(charactersHandler, Mockito.times(1)).loadCharacters(0, 20, "query")
    }

    @Test
    fun searchCharacter_ReturnSuccess_shouldCallLoadCharactersWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        given(charactersHandler.loadCharacters(0, 20))
            .willReturn(Mockito.mock(CharactersResponse::class.java))

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        Mockito.verify(charactersHandler, Mockito.times(1)).loadCharacters(0, 20, "query")
    }

    @Test
    fun searchCharacter_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20)).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        Mockito.verify(observer).onChanged(
            StateData.loading(null)
        )

        Mockito.verify(observer).onChanged(
            StateData.success(success.data.results)
        )
    }

    @Test
    fun searchCharacter_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val e = IOException("No internet connection")
        given(charactersHandler.loadCharacters(0, 20)).willAnswer{throw e}

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        Mockito.verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun searchCharacter_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val e = Exception("Generic erro")
        given(charactersHandler.loadCharacters(0, 20)).willAnswer{throw e}

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        Mockito.verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_AfterSearch_shouldKeepQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20, "query")).willReturn(success)
        given(charactersHandler.loadCharacters(20, 20, "query")).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")
        vm.loadCharacters()

        Mockito.verify(charactersHandler, Mockito.times(1)).loadCharacters( 0, 20, "query",)
        Mockito.verify(charactersHandler, Mockito.times(1)).loadCharacters(20, 20, "query",)
        Mockito.verify(charactersHandler, Mockito.times(0))
            .loadCharacters(anyInt(), anyInt(), isNull())
    }

    @Test
    fun clearSearch_AfterSearch_shouldClearQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val charactersHandler = Mockito.mock(CharactersHandler::class.java)
        val favoritesHandler = Mockito.mock(FavoritesHandler::class.java)
        val success = Mockito.mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0, 0, 0, 0, ArrayList()))
        given(charactersHandler.loadCharacters(0, 20, "query")).willReturn(success)

        vm = AllViewModel(charactersHandler, favoritesHandler, coroutineRule.testDispatcher)

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")
        vm.clearSearch()
        vm.loadCharacters()

        Mockito.verify(charactersHandler, Mockito.times(1)).loadCharacters( 0, 20, "query")
        Mockito.verify(charactersHandler, Mockito.times(1))
            .loadCharacters(anyInt(), anyInt(), isNull())
    }
}